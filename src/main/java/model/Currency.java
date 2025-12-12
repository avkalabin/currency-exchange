package model;

public class Currency {

    private int id;
    private String code;
    private String fUllName;
    private String sign;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getfUllName() {
        return fUllName;
    }

    public void setfUllName(String fUllName) {
        this.fUllName = fUllName;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
