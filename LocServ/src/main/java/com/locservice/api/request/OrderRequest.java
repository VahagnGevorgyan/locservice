package com.locservice.api.request;

/**
 * Created by Vahagn Gevorgyan
 * 18 February 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class OrderRequest extends WebRequest {

    private String ClientPhone_text;
    private String CollDate;
    private String CollTime;
    private int CollTimeOffset;
    private String tariff;
    private int IdLocality;
    private int CollAddrTypeMenu;
    private int CollLandmark;
    private String CollAddressText;
    private int DeliveryAddrTypeMenu;
    private int DeliveryLandmark;
    private String DeliveryAddressText;
    private int OnlinePayment;

    private int CollTerminal;
    private int DeliveryTerminal;
    private String ClientFullName;
    private String IdOrder;
    private String CallerPhone;
    private String PassengerName;
    private String PassengerPhone;
    private int NoSmoking;
    private int useBonus;
    private String hurry;
    private String CityFrom;
    private String Flight;
    private String MetPhone;
    private String Wagon;
    private String Train;
    private String latitude;
    private String longitude;
    private String del_latitude;
    private String del_longitude;
    private String CollComment;
    private String DeliveryComment;
    private String CollPodjed;
    private String DeliveryPodjed;
    private String OrderComment;
    private String device_token;
    private String CollSuburb;
    private String DeliverySuburb;
    private int g_type;
    private int conditioner;
    private int animal;
    private int need_check;
    private int need_wifi;
    private int need_card;
    private int yellow_reg_num;
    private int luggage;
    private int meeting;
    private int g_width;
    private int e_type;
    private int is_exist;
    private String CollAddressId;
    private String DeliveryAddressId;
    private int NoCashPayment;
    private int id_corporation;
    private String child_seats;
    private String CollGeoObject;
    private String DeliveryGeoObject;
    private int id_card;
    private String os_version;
    private String eta;
    private String price_prediction;

    public int getYellow_reg_num() {
        return yellow_reg_num;
    }
    public void setYellow_reg_num(int yellow_reg_num) {
        this.yellow_reg_num = yellow_reg_num;
    }

    public OrderRequest()  {
        super("neworder");
    }

    public String getClientPhone_text() {
        return ClientPhone_text;
    }

    public void setClientPhone_text(String clientPhone_text) {
        ClientPhone_text = clientPhone_text;
    }

    public String getCollDate() {
        return CollDate;
    }

    public void setCollDate(String collDate) {
        CollDate = collDate;
    }

    public String getCollTime() {
        return CollTime;
    }

    public void setCollTime(String collTime) {
        CollTime = collTime;
    }

    public String getTariff() {
        return tariff;
    }

    public void setTariff(String tariff) {
        this.tariff = tariff;
    }

    public int getIdLocality() {
        return IdLocality;
    }

    public void setIdLocality(int idLocality) {
        IdLocality = idLocality;
    }

    public int getCollAddrTypeMenu() {
        return CollAddrTypeMenu;
    }

    public void setCollAddrTypeMenu(int collAddrTypeMenu) {
        CollAddrTypeMenu = collAddrTypeMenu;
    }

    public int getCollLandmark() {
        return CollLandmark;
    }

    public void setCollLandmark(int collLandmark) {
        CollLandmark = collLandmark;
    }

    public String getCollAddressText() {
        return CollAddressText;
    }

    public void setCollAddressText(String collAddressText) {
        CollAddressText = collAddressText;
    }

    public int getDeliveryAddrTypeMenu() {
        return DeliveryAddrTypeMenu;
    }

    public void setDeliveryAddrTypeMenu(int deliveryAddrTypeMenu) {
        DeliveryAddrTypeMenu = deliveryAddrTypeMenu;
    }

    public int getDeliveryLandmark() {
        return DeliveryLandmark;
    }

    public void setDeliveryLandmark(int deliveryLandmark) {
        DeliveryLandmark = deliveryLandmark;
    }

    public String getDeliveryAddressText() {
        return DeliveryAddressText;
    }

    public void setDeliveryAddressText(String deliveryAddressText) {
        DeliveryAddressText = deliveryAddressText;
    }

    public int getOnlinePayment() {
        return OnlinePayment;
    }

    public void setOnlinePayment(int onlinePayment) {
        OnlinePayment = onlinePayment;
    }

    public int getCollTerminal() {
        return CollTerminal;
    }

    public void setCollTerminal(int collTerminal) {
        CollTerminal = collTerminal;
    }

    public int getDeliveryTerminal() {
        return DeliveryTerminal;
    }

    public void setDeliveryTerminal(int deliveryTerminal) {
        DeliveryTerminal = deliveryTerminal;
    }

    public String getClientFullName() {
        return ClientFullName;
    }

    public void setClientFullName(String clientFullName) {
        ClientFullName = clientFullName;
    }

    public String getIdOrder() {
        return IdOrder;
    }

    public void setIdOrder(String idOrder) {
        IdOrder = idOrder;
    }

    public String getCallerPhone() {
        return CallerPhone;
    }

    public void setCallerPhone(String callerPhone) {
        CallerPhone = callerPhone;
    }

    public String getPassengerName() {
        return PassengerName;
    }

    public void setPassengerName(String passengerName) {
        PassengerName = passengerName;
    }

    public String getPassengerPhone() {
        return PassengerPhone;
    }

    public void setPassengerPhone(String passengerPhone) {
        PassengerPhone = passengerPhone;
    }

    public int getNoSmoking() {
        return NoSmoking;
    }

    public void setNoSmoking(int noSmoking) {
        NoSmoking = noSmoking;
    }

    public int getUseBonus() {
        return useBonus;
    }

    public void setUseBonus(int useBonus) {
        this.useBonus = useBonus;
    }

    public String getHurry() {
        return hurry;
    }

    public void setHurry(String hurry) {
        this.hurry = hurry;
    }

    public String getCityFrom() {
        return CityFrom;
    }

    public void setCityFrom(String cityFrom) {
        CityFrom = cityFrom;
    }

    public String getFlight() {
        return Flight;
    }

    public void setFlight(String flight) {
        Flight = flight;
    }

    public String getMetPhone() {
        return MetPhone;
    }

    public void setMetPhone(String metPhone) {
        MetPhone = metPhone;
    }

    public String getWagon() {
        return Wagon;
    }

    public void setWagon(String wagon) {
        Wagon = wagon;
    }

    public String getTrain() {
        return Train;
    }

    public void setTrain(String train) {
        Train = train;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getDel_latitude() {
        return del_latitude;
    }

    public void setDel_latitude(String del_latitude) {
        this.del_latitude = del_latitude;
    }

    public String getDel_longitude() {
        return del_longitude;
    }

    public void setDel_longitude(String del_longitude) {
        this.del_longitude = del_longitude;
    }

    public String getCollComment() {
        return CollComment;
    }

    public void setCollComment(String collComment) {
        CollComment = collComment;
    }

    public String getDeliveryComment() {
        return DeliveryComment;
    }

    public void setDeliveryComment(String deliveryComment) {
        DeliveryComment = deliveryComment;
    }

    public String getOrderComment() {
        return OrderComment;
    }

    public void setOrderComment(String orderComment) {
        OrderComment = orderComment;
    }

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }

    public String getCollSuburb() {
        return CollSuburb;
    }

    public void setCollSuburb(String collSuburb) {
        CollSuburb = collSuburb;
    }

    public String getDeliverySuburb() {
        return DeliverySuburb;
    }

    public void setDeliverySuburb(String deliverySuburb) {
        DeliverySuburb = deliverySuburb;
    }

    public int getG_type() {
        return g_type;
    }

    public void setG_type(int g_type) {
        this.g_type = g_type;
    }

    public int getConditioner() {
        return conditioner;
    }

    public void setConditioner(int conditioner) {
        this.conditioner = conditioner;
    }

    public int getAnimal() {
        return animal;
    }

    public void setAnimal(int animal) {
        this.animal = animal;
    }

    public int getNeed_check() {
        return need_check;
    }

    public void setNeed_check(int need_check) {
        this.need_check = need_check;
    }

    public int getNeed_wifi() {
        return need_wifi;
    }

    public void setNeed_wifi(int need_wifi) {
        this.need_wifi = need_wifi;
    }

    public int getNeed_card() {
        return need_card;
    }

    public void setNeed_card(int need_card) {
        this.need_card = need_card;
    }

    public int getLuggage() {
        return luggage;
    }

    public void setLuggage(int luggage) {
        this.luggage = luggage;
    }

    public int getMeeting() {
        return meeting;
    }

    public void setMeeting(int meeting) {
        this.meeting = meeting;
    }

    public int getG_width() {
        return g_width;
    }

    public void setG_width(int g_width) {
        this.g_width = g_width;
    }

    public int getE_type() {
        return e_type;
    }

    public void setE_type(int e_type) {
        this.e_type = e_type;
    }

    public int getIs_exist() {
        return is_exist;
    }

    public void setIs_exist(int is_exist) {
        this.is_exist = is_exist;
    }

    public String getCollAddressId() {
        return CollAddressId;
    }

    public void setCollAddressId(String collAddressId) {
        CollAddressId = collAddressId;
    }

    public String getDeliveryAddressId() {
        return DeliveryAddressId;
    }

    public void setDeliveryAddressId(String deliveryAddressId) {
        DeliveryAddressId = deliveryAddressId;
    }

    public int getNoCashPayment() {
        return NoCashPayment;
    }

    public void setNoCashPayment(int noCashPayment) {
        NoCashPayment = noCashPayment;
    }

    public int getId_corporation() {
        return id_corporation;
    }

    public void setId_corporation(int id_corporation) {
        this.id_corporation = id_corporation;
    }

    public String getChild_seats() {
        return child_seats;
    }

    public void setChild_seats(String child_seats) {
        this.child_seats = child_seats;
    }

    public String getCollGeoObject() {
        return CollGeoObject;
    }

    public void setCollGeoObject(String collGeoObject) {
        CollGeoObject = collGeoObject;
    }

    public String getDeliveryGeoObject() {
        return DeliveryGeoObject;
    }

    public void setDeliveryGeoObject(String deliveryGeoObject) {
        DeliveryGeoObject = deliveryGeoObject;
    }

    public int getId_card() {
        return id_card;
    }

    public void setId_card(int id_card) {
        this.id_card = id_card;
    }


    public String getCollPodjed() {
        return CollPodjed;
    }

    public void setCollPodjed(String collPodjed) {
        CollPodjed = collPodjed;
    }

    public String getDeliveryPodjed() {
        return DeliveryPodjed;
    }

    public void setDeliveryPodjed(String deliveryPodjed) {
        DeliveryPodjed = deliveryPodjed;
    }

    public int getCollTimeOffset() {
        return CollTimeOffset;
    }

    public void setCollTimeOffset(int collTimeOffset) {
        this.CollTimeOffset = collTimeOffset;
    }

    public String getOs_version() {
        return os_version;
    }

    public void setOs_version(String os_version) {
        this.os_version = os_version;
    }

    public String getEta() {
        return eta;
    }

    public void setEta(String eta) {
        this.eta = eta;
    }

    public String getPrice_prediction() {
        return price_prediction;
    }

    public void setPrice_prediction(String price_prediction) {
        this.price_prediction = price_prediction;
    }
}
