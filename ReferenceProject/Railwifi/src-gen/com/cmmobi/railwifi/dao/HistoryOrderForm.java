package com.cmmobi.railwifi.dao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table HISTORY_ORDER_FORM.
 */
public class HistoryOrderForm {

    private Long id;
    private String user_id;
    /** Not-null value. */
    private String uuid;
    private String rail_num;
    private String site_num;
    private String nick_name;
    private String telephone;
    private String order_time;
    private String total_price;
    private String train_num;
    private String order_code;
    private String status;
    private String content;
    private String eat_position;
    private String site_count;

    public HistoryOrderForm() {
    }

    public HistoryOrderForm(Long id) {
        this.id = id;
    }

    public HistoryOrderForm(Long id, String user_id, String uuid, String rail_num, String site_num, String nick_name, String telephone, String order_time, String total_price, String train_num, String order_code, String status, String content, String eat_position, String site_count) {
        this.id = id;
        this.user_id = user_id;
        this.uuid = uuid;
        this.rail_num = rail_num;
        this.site_num = site_num;
        this.nick_name = nick_name;
        this.telephone = telephone;
        this.order_time = order_time;
        this.total_price = total_price;
        this.train_num = train_num;
        this.order_code = order_code;
        this.status = status;
        this.content = content;
        this.eat_position = eat_position;
        this.site_count = site_count;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    /** Not-null value. */
    public String getUuid() {
        return uuid;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getRail_num() {
        return rail_num;
    }

    public void setRail_num(String rail_num) {
        this.rail_num = rail_num;
    }

    public String getSite_num() {
        return site_num;
    }

    public void setSite_num(String site_num) {
        this.site_num = site_num;
    }

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getOrder_time() {
        return order_time;
    }

    public void setOrder_time(String order_time) {
        this.order_time = order_time;
    }

    public String getTotal_price() {
        return total_price;
    }

    public void setTotal_price(String total_price) {
        this.total_price = total_price;
    }

    public String getTrain_num() {
        return train_num;
    }

    public void setTrain_num(String train_num) {
        this.train_num = train_num;
    }

    public String getOrder_code() {
        return order_code;
    }

    public void setOrder_code(String order_code) {
        this.order_code = order_code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getEat_position() {
        return eat_position;
    }

    public void setEat_position(String eat_position) {
        this.eat_position = eat_position;
    }

    public String getSite_count() {
        return site_count;
    }

    public void setSite_count(String site_count) {
        this.site_count = site_count;
    }

}
