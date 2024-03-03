package de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Objects;

@Entity
@Table(name = "tmf_order_inbox")
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TmfOrderInbox {

    @Id
    private String id;

    @Column(name = "order_id")
    private String orderId;

    @Column(name = "status")
    @Convert(converter = StatusConverter.class)
    @NotNull
    private Status status;

    @Column(name = "message"/*, columnDefinition = "json"*/)
    @NotNull
    private String message; // assuming the message will be stored as JSON string

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TmfOrderInbox that)) return false;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
