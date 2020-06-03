package com.nowcoder.community;

import org.json.JSONObject;

public class AFS {
    public static void main(String[] args) throws  Exception {
        JSONObject o = new JSONObject();
        if (o == null) {
            return ;
        }
        boolean same = true;
        for (String key : o.keySet()) {
            same = false;

            if (!same) {
                break;
            }
        }

    }
}
