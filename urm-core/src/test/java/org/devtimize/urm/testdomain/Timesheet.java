package org.devtimize.urm.testdomain;

import org.devtimize.urm.testdomain.person.Employee;

import java.util.List;

public class Timesheet {
    private final Employee who;
    private final Task task;
    private Integer hours;
    private List<String> stuff;

    public Timesheet(final Employee who, final Task task, final Integer hours) {
        this.who = who;
        this.task = task;
        this.hours = hours;
    }

    public Employee getWho() {
        return who;
    }

    public Task getTask() {
        return task;
    }

    public Integer getHours() {
        return hours;
    }

    /**
     * Manager can alter hours before closing task
     *
     * @param hours New amount of hours
     */
    public void alterHours(final Integer hours) {
        this.hours = hours;
    }

    @Override
    public String toString() {
        return "Timesheet [who=" + who + ", task=" + task + ", hours=" + hours + "]";
    }
}
