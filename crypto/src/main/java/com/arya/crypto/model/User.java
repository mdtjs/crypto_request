package com.arya.crypto.model;

import lombok.*;

import java.io.Serializable;

/**
 * @author Arya
 * @version v1.0
 * @since v1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class User implements Serializable {

    private String name;
    private String idCard;
    private Long timestamp;
}
