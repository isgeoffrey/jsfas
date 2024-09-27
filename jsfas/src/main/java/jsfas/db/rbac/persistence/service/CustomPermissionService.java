package jsfas.db.rbac.persistence.service;

import java.util.Set;

public interface CustomPermissionService {

    public boolean checkDepartment(String entityAction, Set<String> deptSet);
}
