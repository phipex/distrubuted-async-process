package co.com.ies.pruebas.webservice;

import jodd.util.concurrent.Task;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@Data
public class PendingTask<TypeTask> implements Serializable {

    public TypeTask task;
    public String manageFor;


    public PendingTask() {
    }

    public PendingTask(TypeTask task) {
        this.task = task;
    }
}
