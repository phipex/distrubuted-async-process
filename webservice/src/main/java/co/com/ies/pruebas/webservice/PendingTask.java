package co.com.ies.pruebas.webservice;

import jodd.util.concurrent.Task;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

public class PendingTask<TypeTask> implements Serializable {

    public TypeTask task;
    public String manageFor;


    public PendingTask() {
    }

    public PendingTask(TypeTask task) {
        this.task = task;
    }

    public TypeTask getTask() {
        return task;
    }

    public void setTask(TypeTask task) {
        this.task = task;
    }

    public String getManageFor() {
        return manageFor;
    }

    public void setManageFor(String manageFor) {
        this.manageFor = manageFor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PendingTask<?> that = (PendingTask<?>) o;
        return Objects.equals(task, that.task);
    }

    @Override
    public int hashCode() {
        return Objects.hash(task);
    }

    @Override
    public String toString() {
        return "PendingTask{" +
                "task=" + task +
                ", manageFor='" + manageFor + '\'' +
                '}';
    }
}
