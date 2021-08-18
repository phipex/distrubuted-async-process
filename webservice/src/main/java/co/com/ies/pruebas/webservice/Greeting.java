package co.com.ies.pruebas.webservice;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "task")
public  class Greeting implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;
    private String content;
    private String ip;

    @Column(name = "ip_tramited")
    private String ipTramited;

    public Greeting(){}

    public Greeting(String content, String ip) {
        this.content = content;
        this.ip = ip;
    }

    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public String getIp(){
        return ip;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIpTramited() {
        return ipTramited;
    }

    public void setIpTramited(String ipTramited) {
        this.ipTramited = ipTramited;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Greeting greeting = (Greeting) o;
        return Objects.equals(id, greeting.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, content, ip, ipTramited);
    }

    @Override
    public String toString() {
        return "Greeting{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", ip='" + ip + '\'' +
                ", ipTramited='" + ipTramited + '\'' +
                '}';
    }
}
