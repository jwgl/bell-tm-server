package cn.edu.bnuz.bell.security

import org.apache.commons.lang.builder.HashCodeBuilder

/**
 * 用户应用角色
 * @author Yang Lin
 */
class UserAppRole implements Serializable {
    private static final long serialVersionUID = 1

    String userId
    String roleId

    static mapping = {
        comment     '用户-应用-角色'
        table       'dv_user_app_role'
        id          composite: ['roleId', 'userId'], comment: '用户应用角色ID'
        userId      comment: '用户'
        roleId      comment: '角色'
    }

    boolean equals(other) {
        if (!(other instanceof UserRole)) {
            return false
        }

        other.userId == userId && other.roleId == roleId
    }

    int hashCode() {
        def builder = new HashCodeBuilder()
        builder.append(userId)
        builder.append(roleId)
        builder.toHashCode()
    }

    static List<String> getUserRoles(String userId) {
        UserAppRole.executeQuery "select roleId from UserAppRole where userId=:userId", [userId: userId]
    }
}
