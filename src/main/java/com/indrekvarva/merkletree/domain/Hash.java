package com.indrekvarva.merkletree.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor(staticName = "of")
@EqualsAndHashCode
public class Hash {

    private final byte[] value;

    @Override
    public String toString() {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < this.value.length; i++) {
            String hex = Integer.toHexString(0xff & this.value[i]);
            if(hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

}
