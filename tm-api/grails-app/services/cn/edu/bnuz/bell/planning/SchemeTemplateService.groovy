package cn.edu.bnuz.bell.planning

import grails.transaction.Transactional

@Transactional
class SchemeTemplateService {

    def getList() {
        SchemeTemplate.executeQuery '''
select new Map(
  id as id,
  name as name
)
from SchemeTemplate
order by id desc
'''
    }
}
