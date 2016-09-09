package cn.edu.bnuz.bell.card

import cn.edu.bnuz.bell.workflow.AuditAction
import groovy.util.logging.Log4j

/**
 * 补办学生班状态
 * @author Yang Lin
 */
@Log4j()
enum CardReissueStatus {
    NULL     (-1, "空"),
    CREATED  (0, "新建"),
    COMMITTED(1, "待审核"),
    APPROVED (2, "已审核"),
    MAKING   (3, "制作中"),
    REJECTED (4, "退回"),
    FINISHED (5, "完成")

    final Integer id
    final String name

    private CardReissueStatus(int id, String name) {
        this.id = id
        this.name = name
    }

    private static stateMatrix = [
            (CREATED): [
                    (AuditAction.UPDATE): CREATED,    // 编辑
                    (AuditAction.DELETE): NULL,       // 删除
                    (AuditAction.COMMIT): COMMITTED,  // 提交
            ],
            (COMMITTED): [
                    (AuditAction.CANCEL): CREATED,    // 取消
                    (AuditAction.ACCEPT): APPROVED,   // 已审核
                    (AuditAction.REJECT): REJECTED,   // 退回
            ],
            (APPROVED): [
                    (AuditAction.ACCEPT): MAKING,     // 制作中
            ],
            (REJECTED) : [
                    (AuditAction.UPDATE): CREATED,    // 编辑
                    (AuditAction.DELETE): NULL,       // 删除
                    (AuditAction.COMMIT): COMMITTED,  // 提交
            ],
            (MAKING)  : [
                    (AuditAction.ACCEPT): FINISHED,   // 入库
                    (AuditAction.REJECT): APPROVED,   // 删除订单项
            ],
            (FINISHED) : [
                    (AuditAction.REVOKE): MAKING,     // 取消入库
            ]
    ]

    boolean allow(AuditAction action) {
        stateMatrix[this].containsKey(action)
    }

    CardReissueStatus next(AuditAction action) {
        stateMatrix[this][action]?: this
    }
}