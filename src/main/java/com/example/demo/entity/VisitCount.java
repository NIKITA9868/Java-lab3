package com.example.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.concurrent.atomic.AtomicLong;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;


@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "visit_counts")
public class VisitCount {
    @jakarta.persistence.Id
    @Id
    private String url;
    private AtomicLong count = new AtomicLong(0);


    public VisitCount(String url) {
        this.url = url;
    }
}
