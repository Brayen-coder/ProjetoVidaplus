package br.com.vidaplus.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
public class AuditLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String method;
    private String path;
    private Instant timestamp;

    public AuditLog(){}
    public AuditLog(String username, String method, String path, Instant timestamp){
        this.username = username; this.method = method; this.path = path; this.timestamp = timestamp;
    }

    public Long getId(){ return id; }
    public String getUsername(){ return username; }
    public String getMethod(){ return method; }
    public String getPath(){ return path; }
    public Instant getTimestamp(){ return timestamp; }
}
