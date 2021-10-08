package org.devtimize.urm.testdomain;

import org.devtimize.urm.testdomain.person.Manager;
import org.devtimize.urm.testdomain.person.Employee;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Task {
    private final List<Employee> assignedEmployees = new ArrayList<>();
    private final Manager manager;
    private boolean completed;
    private final String description;

    public Task(final String description, final Manager manager, final Employee... employees) {
        this.description = description;
        this.manager = manager;
        assignedEmployees.addAll(Arrays.asList(employees));
        completed = false;
    }

    public Manager getManager() {
        return manager;
    }

    public List<Employee> getAssignedEmployees() {
        return assignedEmployees;
    }

    public void addEmployee(final Employee e) {
        assignedEmployees.add(e);
    }

    public void removeEmployee(final Employee e) {
        assignedEmployees.remove(e);
    }

    public void completeTask() {
        completed = true;
    }
}
