package cn.edu.bnuz.bell.card

import cn.edu.bnuz.bell.http.BadRequestException
import cn.edu.bnuz.bell.http.NotFoundException
import cn.edu.bnuz.bell.organization.Teacher
import cn.edu.bnuz.bell.workflow.AuditAction
import grails.transaction.Transactional

@Transactional
class CardReissueOrderService {
    def getAll() {
        CardReissueOrder.executeQuery """
select new map(
  o.id as id,
  (select count(*) from CardReissueOrderItem oi where oi.order = o) as totalCount,
  (select count(*) from CardReissueOrderItem oi where oi.order = o and oi.form.status = 4) as finishedCount,
  creator.name as creatorName,
  o.dateCreated as dateCreated,
  modifier.name as modifierName,
  o.dateModified as dateModified
)
from CardReissueOrder o
join o.creator creator
left join o.modifier modifier
order by o.id desc
"""
    }

    def getInfo(Long id) {
        def results = CardReissueOrder.executeQuery """
select new map(
  o.id as id,
  creator.name as creatorName,
  o.dateCreated as dateCreated,
  modifier.name as modifierName,
  o.dateModified as dateModified
)
from CardReissueOrder o
join o.creator creator
left join o.modifier modifier
where o.id = :id
""", [id: id]

        if (!results) {
            throw new NotFoundException()
        }

        def order = results[0]
        order.items = CardReissueOrderItem.executeQuery """
select new map(
  oi.id as id,
  form.id as formId,
  form.dateModified as applyDate,
  form.status as status,
  student.id as studentId,
  student.name as name,
  student.sex as sex,
  admission.fromProvince as province,
  student.birthday as birthday,
  concat(major.grade + subject.lengthOfSchooling, '-7-1') as validDate,
  adminClass.name as adminClass,
  department.name as department,
  subject.name as subject
)
from CardReissueOrderItem oi
join oi.form form
join form.student student
join student.admission admission
join student.adminClass adminClass
join student.department department
join student.major major
join major.subject subject
where oi.order.id = :id
""", [id: id]

        return order
    }

    CardReissueOrder create(String teacherId, CardReissueOrderCommand cmd) {
        CardReissueOrder order = new CardReissueOrder(
                creator: Teacher.load(teacherId),
                dateModified: new Date(),
        )

        cmd.addedItems.each {
            // 更新申请状态为处理中
            def form = CardReissueForm.get(it.formId)
            if (form.status == CardReissueStatus.APPROVED && form.status.allow(AuditAction.ACCEPT)) {
                form.status = form.status.next(AuditAction.ACCEPT)
                form.save()
                // 添加订单项
                def orderItem = new CardReissueOrderItem(form: form)
                order.addToItems(orderItem)
            }
        }

        order.save()
    }

    void update(String teacherId, CardReissueOrderCommand cmd) {
        CardReissueOrder order = CardReissueOrder.get(cmd.id)
        log.debug((cmd.id))
        order.modifier = Teacher.load(teacherId)
        order.dateModified = new Date()

        cmd.addedItems.each {
            def form = CardReissueForm.get(it.formId)
            log.debug(form.status)
            if (form.status == CardReissueStatus.APPROVED && form.status.allow(AuditAction.ACCEPT)) {
                form.status = form.status.next(AuditAction.ACCEPT)
                form.save()
                def orderItem = new CardReissueOrderItem(form: form)
                order.addToItems(orderItem)
            }
        }

        cmd.removedItems.each {
            // 更新申请状态为新申请
            def form = CardReissueForm.get(it.formId)
            if (form.status == CardReissueStatus.MAKING && form.status.allow(AuditAction.REJECT)) {
                form.status = form.status.next(AuditAction.REJECT)
                form.save()
                def orderItem = CardReissueOrderItem.load(it.id)
                order.removeFromItems(orderItem)
                orderItem.delete()
            }
        }

        order.save()
    }

    void delete(Long id) {
        def order = CardReissueOrder.get(id)
        if (!order) {
            throw new NotFoundException();
        }

        boolean allowStatus = order.items.every { item ->
            item.form.status.allow(AuditAction.REJECT)
        }

        if (!allowStatus) {
            throw new BadRequestException()
        }

        order.items.each { item ->
            item.form.status = item.form.status.next(AuditAction.REJECT)
            item.form.save()
        }

        order.delete()
    }

    void receive(Long formId, boolean received) {
        def form = CardReissueForm.get(formId)
        def action = received ? AuditAction.ACCEPT : AuditAction.REJECT;
        if (!form.status.allow(action)) {
            throw new BadRequestException()
        }

        form.status = form.status.next(action)
        form.save()
    }

    void receiveAll(Long id, boolean received) {
        CardReissueOrder.executeUpdate """
update CardReissueForm
set status = :newStatus
where id in (
  select oi.form.id
  from CardReissueOrder o
  join o.items oi
  where o.id = :id
) and status = :oldStatus
""", [id       : id,
      oldStatus: received ? CardReissueStatus.APPROVED : CardReissueStatus.FINISHED,
      newStatus: received ? CardReissueStatus.FINISHED : CardReissueStatus.APPROVED]
    }
}
