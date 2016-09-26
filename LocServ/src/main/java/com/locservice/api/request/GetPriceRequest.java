package com.locservice.api.request;

/**
 * Created by Vahagn Gevorgyan
 * 6 April 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class GetPriceRequest extends WebRequest {

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

    private float distance;   // km
    private int duration;   // min

    private float service_way2; // Movement in the country (km)
    private float service_way3; // Submission of the city (km)


    public GetPriceRequest() {
        super("getprice");
    }

    public void setClientPhone_text(String clientPhone_text) {
        ClientPhone_text = clientPhone_text;
    }

    public void setCollDate(String collDate) {
        CollDate = collDate;
    }

    public void setCollTime(String collTime) {
        CollTime = collTime;
    }

    public void setCollTimeOffset(int collTimeOffset) {
        CollTimeOffset = collTimeOffset;
    }

    public void setTariff(String tariff) {
        this.tariff = tariff;
    }

    public void setIdLocality(int idLocality) {
        IdLocality = idLocality;
    }

    public void setCollAddrTypeMenu(int collAddrTypeMenu) {
        CollAddrTypeMenu = collAddrTypeMenu;
    }

    public void setCollLandmark(int collLandmark) {
        CollLandmark = collLandmark;
    }

    public void setCollAddressText(String collAddressText) {
        CollAddressText = collAddressText;
    }

    public void setDeliveryAddrTypeMenu(int deliveryAddrTypeMenu) {
        DeliveryAddrTypeMenu = deliveryAddrTypeMenu;
    }

    public void setDeliveryLandmark(int deliveryLandmark) {
        DeliveryLandmark = deliveryLandmark;
    }

    public void setDeliveryAddressText(String deliveryAddressText) {
        DeliveryAddressText = deliveryAddressText;
    }

    public void setOnlinePayment(int onlinePayment) {
        OnlinePayment = onlinePayment;
    }

    public void setCollTerminal(int collTerminal) {
        CollTerminal = collTerminal;
    }

    public void setDeliveryTerminal(int deliveryTerminal) {
        DeliveryTerminal = deliveryTerminal;
    }

    public void setClientFullName(String clientFullName) {
        ClientFullName = clientFullName;
    }

    public void setIdOrder(String idOrder) {
        IdOrder = idOrder;
    }

    public void setCallerPhone(String callerPhone) {
        CallerPhone = callerPhone;
    }

    public void setPassengerName(String passengerName) {
        PassengerName = passengerName;
    }

    public void setPassengerPhone(String passengerPhone) {
        PassengerPhone = passengerPhone;
    }

    public void setNoSmoking(int noSmoking) {
        NoSmoking = noSmoking;
    }

    public void setUseBonus(int useBonus) {
        this.useBonus = useBonus;
    }

    public void setHurry(String hurry) {
        this.hurry = hurry;
    }

    public void setCityFrom(String cityFrom) {
        CityFrom = cityFrom;
    }

    public void setFlight(String flight) {
        Flight = flight;
    }

    public void setMetPhone(String metPhone) {
        MetPhone = metPhone;
    }

    public void setWagon(String wagon) {
        Wagon = wagon;
    }

    public void setTrain(String train) {
        Train = train;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setDel_latitude(String del_latitude) {
        this.del_latitude = del_latitude;
    }

    public void setDel_longitude(String del_longitude) {
        this.del_longitude = del_longitude;
    }

    public void setCollComment(String collComment) {
        CollComment = collComment;
    }

    public void setDeliveryComment(String deliveryComment) {
        DeliveryComment = deliveryComment;
    }

    public void setCollPodjed(String collPodjed) {
        CollPodjed = collPodjed;
    }

    public void setDeliveryPodjed(String deliveryPodjed) {
        DeliveryPodjed = deliveryPodjed;
    }

    public void setOrderComment(String orderComment) {
        OrderComment = orderComment;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }

    public void setCollSuburb(String collSuburb) {
        CollSuburb = collSuburb;
    }

    public void setDeliverySuburb(String deliverySuburb) {
        DeliverySuburb = deliverySuburb;
    }

    public void setG_type(int g_type) {
        this.g_type = g_type;
    }

    public void setConditioner(int conditioner) {
        this.conditioner = conditioner;
    }

    public void setAnimal(int animal) {
        this.animal = animal;
    }

    public void setNeed_check(int need_check) {
        this.need_check = need_check;
    }

    public void setNeed_wifi(int need_wifi) {
        this.need_wifi = need_wifi;
    }

    public void setNeed_card(int need_card) {
        this.need_card = need_card;
    }

    public void setYellow_reg_num(int yellow_reg_num) {
        this.yellow_reg_num = yellow_reg_num;
    }

    public void setLuggage(int luggage) {
        this.luggage = luggage;
    }

    public void setMeeting(int meeting) {
        this.meeting = meeting;
    }

    public void setG_width(int g_width) {
        this.g_width = g_width;
    }

    public void setE_type(int e_type) {
        this.e_type = e_type;
    }

    public void setIs_exist(int is_exist) {
        this.is_exist = is_exist;
    }

    public void setCollAddressId(String collAddressId) {
        CollAddressId = collAddressId;
    }

    public void setDeliveryAddressId(String deliveryAddressId) {
        DeliveryAddressId = deliveryAddressId;
    }

    public void setNoCashPayment(int noCashPayment) {
        NoCashPayment = noCashPayment;
    }

    public void setId_corporation(int id_corporation) {
        this.id_corporation = id_corporation;
    }

    public void setChild_seats(String child_seats) {
        this.child_seats = child_seats;
    }

    public void setCollGeoObject(String collGeoObject) {
        CollGeoObject = collGeoObject;
    }

    public void setDeliveryGeoObject(String deliveryGeoObject) {
        DeliveryGeoObject = deliveryGeoObject;
    }

    public void setId_card(int id_card) {
        this.id_card = id_card;
    }

    public void setOs_version(String os_version) {
        this.os_version = os_version;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getClientPhone_text() {
        return ClientPhone_text;
    }

    public String getCollDate() {
        return CollDate;
    }

    public String getCollTime() {
        return CollTime;
    }

    public int getCollTimeOffset() {
        return CollTimeOffset;
    }

    public String getTariff() {
        return tariff;
    }

    public int getIdLocality() {
        return IdLocality;
    }

    public int getCollAddrTypeMenu() {
        return CollAddrTypeMenu;
    }

    public int getCollLandmark() {
        return CollLandmark;
    }

    public String getCollAddressText() {
        return CollAddressText;
    }

    public int getDeliveryAddrTypeMenu() {
        return DeliveryAddrTypeMenu;
    }

    public int getDeliveryLandmark() {
        return DeliveryLandmark;
    }

    public String getDeliveryAddressText() {
        return DeliveryAddressText;
    }

    public int getOnlinePayment() {
        return OnlinePayment;
    }

    public int getCollTerminal() {
        return CollTerminal;
    }

    public int getDeliveryTerminal() {
        return DeliveryTerminal;
    }

    public String getClientFullName() {
        return ClientFullName;
    }

    public String getIdOrder() {
        return IdOrder;
    }

    public String getCallerPhone() {
        return CallerPhone;
    }

    public String getPassengerName() {
        return PassengerName;
    }

    public String getPassengerPhone() {
        return PassengerPhone;
    }

    public int getNoSmoking() {
        return NoSmoking;
    }

    public int getUseBonus() {
        return useBonus;
    }

    public String getHurry() {
        return hurry;
    }

    public String getCityFrom() {
        return CityFrom;
    }

    public String getFlight() {
        return Flight;
    }

    public String getMetPhone() {
        return MetPhone;
    }

    public String getWagon() {
        return Wagon;
    }

    public String getTrain() {
        return Train;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getDel_latitude() {
        return del_latitude;
    }

    public String getDel_longitude() {
        return del_longitude;
    }

    public String getCollComment() {
        return CollComment;
    }

    public String getDeliveryComment() {
        return DeliveryComment;
    }

    public String getCollPodjed() {
        return CollPodjed;
    }

    public String getDeliveryPodjed() {
        return DeliveryPodjed;
    }

    public String getOrderComment() {
        return OrderComment;
    }

    public String getDevice_token() {
        return device_token;
    }

    public String getCollSuburb() {
        return CollSuburb;
    }

    public String getDeliverySuburb() {
        return DeliverySuburb;
    }

    public int getG_type() {
        return g_type;
    }

    public int getConditioner() {
        return conditioner;
    }

    public int getAnimal() {
        return animal;
    }

    public int getNeed_check() {
        return need_check;
    }

    public int getNeed_wifi() {
        return need_wifi;
    }

    public int getNeed_card() {
        return need_card;
    }

    public int getYellow_reg_num() {
        return yellow_reg_num;
    }

    public int getLuggage() {
        return luggage;
    }

    public int getMeeting() {
        return meeting;
    }

    public int getG_width() {
        return g_width;
    }

    public int getE_type() {
        return e_type;
    }

    public int getIs_exist() {
        return is_exist;
    }

    public String getCollAddressId() {
        return CollAddressId;
    }

    public String getDeliveryAddressId() {
        return DeliveryAddressId;
    }

    public int getNoCashPayment() {
        return NoCashPayment;
    }

    public int getId_corporation() {
        return id_corporation;
    }

    public String getChild_seats() {
        return child_seats;
    }

    public String getCollGeoObject() {
        return CollGeoObject;
    }

    public String getDeliveryGeoObject() {
        return DeliveryGeoObject;
    }

    public int getId_card() {
        return id_card;
    }

    public String getOs_version() {
        return os_version;
    }

    public float getService_way2() {
        return service_way2;
    }

    public void setService_way2(float service_way2) {
        this.service_way2 = service_way2;
    }

    public float getService_way3() {
        return service_way3;
    }

    public void setService_way3(float service_way3) {
        this.service_way3 = service_way3;
    }
}
