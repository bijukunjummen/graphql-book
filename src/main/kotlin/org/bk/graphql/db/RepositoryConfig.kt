package org.bk.graphql.db

import com.google.common.base.CaseFormat
import com.infobip.spring.data.jdbc.annotation.processor.ProjectColumnCaseFormat

@ProjectColumnCaseFormat(CaseFormat.LOWER_UNDERSCORE)
class RepositoryConfig {

}