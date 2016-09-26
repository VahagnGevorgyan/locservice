package com.locservice.api.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vahagn Gevorgyan
 * 18 February 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class Order implements Parcelable{

    @SerializedName("IdOrder")
    @Expose
    private String IdOrder;
    @SerializedName("PassengerName")
    @Expose
    private String PassengerName;
    @SerializedName("PassengerPhone")
    @Expose
    private String PassengerPhone;
    @SerializedName("CollDate")
    @Expose
    private String CollDate;
    @SerializedName("CollTime")
    @Expose
    private String CollTime;
    @SerializedName("tariff")
    @Expose
    private String tariff;
    @SerializedName("NoSmoking")
    @Expose
    private int NoSmoking;
    @SerializedName("useBonus")
    @Expose
    private int useBonus;
    @SerializedName("CollAddressId")
    @Expose
    private String CollAddressId;
    @SerializedName("CollPodjed")
    @Expose
    private String CollPodjed;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("CollAddrTypeMenu")
    @Expose
    private String CollAddrTypeMenu;
    @SerializedName("CollLandmark")
    @Expose
    private String CollLandmark;
    @SerializedName("CollAddressText")
    @Expose
    private String CollAddressText;
    @SerializedName("CityFrom")
    @Expose
    private String CityFrom;
    @SerializedName("Flight")
    @Expose
    private String Flight;
    @SerializedName("CollTerminal")
    @Expose
    private String CollTerminal;
    @SerializedName("MetPhone")
    @Expose
    private String MetPhone;
    @SerializedName("Wagon")
    @Expose
    private String Wagon;
    @SerializedName("Train")
    @Expose
    private String Train;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("del_latitude")
    @Expose
    private String delLatitude;
    @SerializedName("del_longitude")
    @Expose
    private String delLongitude;
    @SerializedName("CollComment")
    @Expose
    private String CollComment;
    @SerializedName("DeliveryAddressId")
    @Expose
    private String DeliveryAddressId;
    @SerializedName("DeliveryPodjed")
    @Expose
    private String DeliveryPodjed;
    @SerializedName("DeliveryAddrTypeMenu")
    @Expose
    private String DeliveryAddrTypeMenu;
    @SerializedName("DeliveryLandmark")
    @Expose
    private String DeliveryLandmark;
    @SerializedName("DeliveryTerminal")
    @Expose
    private String DeliveryTerminal;
    @SerializedName("DeliveryAddressText")
    @Expose
    private String DeliveryAddressText;
    @SerializedName("DeliveryComment")
    @Expose
    private String DeliveryComment;
    @SerializedName("OrderComment")
    @Expose
    private String OrderComment;
    @SerializedName("g_type")
    @Expose
    private String gType;
    @SerializedName("conditioner")
    @Expose
    private String conditioner;
    @SerializedName("animal")
    @Expose
    private String animal;
    @SerializedName("need_check")
    @Expose
    private String needCheck;
    @SerializedName("need_wifi")
    @Expose
    private String needWifi;
    @SerializedName("need_card")
    @Expose
    private String needCard;
    @SerializedName("luggage")
    @Expose
    private String luggage;
    @SerializedName("meeting")
    @Expose
    private String meeting;
    @SerializedName("g_width")
    @Expose
    private String gWidth;
    @SerializedName("e_type")
    @Expose
    private String eType;
    @SerializedName("OnlinePayment")
    @Expose
    private String OnlinePayment;
    @SerializedName("price")
    @Expose
    private int price;
    @SerializedName("NoCashPayment")
    @Expose
    private int NoCashPayment;
    @SerializedName("bonus")
    @Expose
    private double bonus;
    @SerializedName("IdLocality")
    @Expose
    private int IdLocality;
    @SerializedName("triptime")
    @Expose
    private int triptime;
    @SerializedName("cityName")
    @Expose
    private String cityName;
    @SerializedName("tariffName")
    @Expose
    private String tariffName;
    @SerializedName("rating")
    @Expose
    private int rating;
    @SerializedName("yellow_reg_num")
    @Expose
    private String yellow_reg_num;
    @SerializedName("hurry")
    @Expose
    private int hurry;
    @SerializedName("child_seats")
    @Expose
    private List<ChildSeatData> childSeats = new ArrayList<ChildSeatData>();
    @SerializedName("id_card")
    @Expose
    private int id_card;
    @SerializedName("feedback")
    @Expose
    private String feedback;


    public Order () {

    }

    protected Order(Parcel in) {
        IdOrder = in.readString();
        PassengerName = in.readString();
        PassengerPhone = in.readString();
        CollDate = in.readString();
        CollTime = in.readString();
        tariff = in.readString();
        NoSmoking = in.readInt();
        useBonus = in.readInt();
        CollAddressId = in.readString();
        CollPodjed = in.readString();
        status = in.readString();
        CollAddrTypeMenu = in.readString();
        CollLandmark = in.readString();
        CollAddressText = in.readString();
        CityFrom = in.readString();
        Flight = in.readString();
        CollTerminal = in.readString();
        MetPhone = in.readString();
        Wagon = in.readString();
        Train = in.readString();
        latitude = in.readString();
        longitude = in.readString();
        delLatitude = in.readString();
        delLongitude = in.readString();
        CollComment = in.readString();
        DeliveryAddressId = in.readString();
        DeliveryPodjed = in.readString();
        DeliveryAddrTypeMenu = in.readString();
        DeliveryLandmark = in.readString();
        DeliveryTerminal = in.readString();
        DeliveryAddressText = in.readString();
        DeliveryComment = in.readString();
        OrderComment = in.readString();
        gType = in.readString();
        conditioner = in.readString();
        animal = in.readString();
        needCheck = in.readString();
        needWifi = in.readString();
        needCard = in.readString();
        luggage = in.readString();
        meeting = in.readString();
        gWidth = in.readString();
        eType = in.readString();
        OnlinePayment = in.readString();
        price = in.readInt();
        NoCashPayment = in.readInt();
        bonus = in.readDouble();
        IdLocality = in.readInt();
        triptime = in.readInt();
        cityName = in.readString();
        tariffName = in.readString();
        rating = in.readInt();
        yellow_reg_num = in.readString();
        hurry = in.readInt();
        childSeats = in.createTypedArrayList(ChildSeatData.CREATOR);
        id_card = in.readInt();
        feedback = in.readString();
    }

    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };

    /**
     *
     * @return
     * The IdOrder
     */
    public String getIdOrder() {
        return IdOrder;
    }

    /**
     *
     * @param IdOrder
     * The IdOrder
     */
    public void setIdOrder(String IdOrder) {
        this.IdOrder = IdOrder;
    }

    /**
     *
     * @return
     * The PassengerName
     */
    public String getPassengerName() {
        return PassengerName;
    }

    /**
     *
     * @param PassengerName
     * The PassengerName
     */
    public void setPassengerName(String PassengerName) {
        this.PassengerName = PassengerName;
    }

    /**
     *
     * @return
     * The PassengerPhone
     */
    public String getPassengerPhone() {
        return PassengerPhone;
    }

    /**
     *
     * @param PassengerPhone
     * The PassengerPhone
     */
    public void setPassengerPhone(String PassengerPhone) {
        this.PassengerPhone = PassengerPhone;
    }

    /**
     *
     * @return
     * The CollDate
     */
    public String getCollDate() {
        return CollDate;
    }

    /**
     *
     * @param CollDate
     * The CollDate
     */
    public void setCollDate(String CollDate) {
        this.CollDate = CollDate;
    }

    /**
     *
     * @return
     * The CollTime
     */
    public String getCollTime() {
        return CollTime;
    }

    /**
     *
     * @param CollTime
     * The CollTime
     */
    public void setCollTime(String CollTime) {
        this.CollTime = CollTime;
    }

    /**
     *
     * @return
     * The tariff
     */
    public String getTariff() {
        return tariff;
    }

    /**
     *
     * @param tariff
     * The tariff
     */
    public void setTariff(String tariff) {
        this.tariff = tariff;
    }

    /**
     *
     * @return
     * The NoSmoking
     */
    public int getNoSmoking() {
        return NoSmoking;
    }

    /**
     *
     * @param NoSmoking
     * The NoSmoking
     */
    public void setNoSmoking(int NoSmoking) {
        this.NoSmoking = NoSmoking;
    }

    /**
     *
     * @return
     * The useBonus
     */
    public int getUseBonus() {
        return useBonus;
    }

    /**
     *
     * @param useBonus
     * The useBonus
     */
    public void setUseBonus(int useBonus) {
        this.useBonus = useBonus;
    }

    /**
     *
     * @return
     * The CollAddressId
     */
    public String getCollAddressId() {
        return CollAddressId;
    }

    /**
     *
     * @param CollAddressId
     * The CollAddressId
     */
    public void setCollAddressId(String CollAddressId) {
        this.CollAddressId = CollAddressId;
    }

    /**
     *
     * @return
     * The CollPodjed
     */
    public String getCollPodjed() {
        return CollPodjed;
    }

    /**
     *
     * @param CollPodjed
     * The CollPodjed
     */
    public void setCollPodjed(String CollPodjed) {
        this.CollPodjed = CollPodjed;
    }

    /**
     *
     * @return
     * The status
     */
    public String getStatus() {
        return status;
    }

    /**
     *
     * @param status
     * The status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     *
     * @return
     * The CollAddrTypeMenu
     */
    public String getCollAddrTypeMenu() {
        return CollAddrTypeMenu;
    }

    /**
     *
     * @param CollAddrTypeMenu
     * The CollAddrTypeMenu
     */
    public void setCollAddrTypeMenu(String CollAddrTypeMenu) {
        this.CollAddrTypeMenu = CollAddrTypeMenu;
    }

    /**
     *
     * @return
     * The CollLandmark
     */
    public String getCollLandmark() {
        return CollLandmark;
    }

    /**
     *
     * @param CollLandmark
     * The CollLandmark
     */
    public void setCollLandmark(String CollLandmark) {
        this.CollLandmark = CollLandmark;
    }

    /**
     *
     * @return
     * The CollAddressText
     */
    public String getCollAddressText() {
        return CollAddressText;
    }

    /**
     *
     * @param CollAddressText
     * The CollAddressText
     */
    public void setCollAddressText(String CollAddressText) {
        this.CollAddressText = CollAddressText;
    }

    /**
     *
     * @return
     * The CityFrom
     */
    public String getCityFrom() {
        return CityFrom;
    }

    /**
     *
     * @param CityFrom
     * The CityFrom
     */
    public void setCityFrom(String CityFrom) {
        this.CityFrom = CityFrom;
    }

    /**
     *
     * @return
     * The Flight
     */
    public String getFlight() {
        return Flight;
    }

    /**
     *
     * @param Flight
     * The Flight
     */
    public void setFlight(String Flight) {
        this.Flight = Flight;
    }

    /**
     *
     * @return
     * The CollTerminal
     */
    public String getCollTerminal() {
        return CollTerminal;
    }

    /**
     *
     * @param CollTerminal
     * The CollTerminal
     */
    public void setCollTerminal(String CollTerminal) {
        this.CollTerminal = CollTerminal;
    }

    /**
     *
     * @return
     * The MetPhone
     */
    public String getMetPhone() {
        return MetPhone;
    }

    /**
     *
     * @param MetPhone
     * The MetPhone
     */
    public void setMetPhone(String MetPhone) {
        this.MetPhone = MetPhone;
    }

    /**
     *
     * @return
     * The Wagon
     */
    public String getWagon() {
        return Wagon;
    }

    /**
     *
     * @param Wagon
     * The Wagon
     */
    public void setWagon(String Wagon) {
        this.Wagon = Wagon;
    }

    /**
     *
     * @return
     * The Train
     */
    public String getTrain() {
        return Train;
    }

    /**
     *
     * @param Train
     * The Train
     */
    public void setTrain(String Train) {
        this.Train = Train;
    }

    /**
     *
     * @return
     * The latitude
     */
    public String getLatitude() {
        return latitude;
    }

    /**
     *
     * @param latitude
     * The latitude
     */
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    /**
     *
     * @return
     * The longitude
     */
    public String getLongitude() {
        return longitude;
    }

    /**
     *
     * @param longitude
     * The longitude
     */
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    /**
     *
     * @return
     * The delLatitude
     */
    public String getDelLatitude() {
        return delLatitude;
    }

    /**
     *
     * @param delLatitude
     * The del_latitude
     */
    public void setDelLatitude(String delLatitude) {
        this.delLatitude = delLatitude;
    }

    /**
     *
     * @return
     * The delLongitude
     */
    public String getDelLongitude() {
        return delLongitude;
    }

    /**
     *
     * @param delLongitude
     * The del_longitude
     */
    public void setDelLongitude(String delLongitude) {
        this.delLongitude = delLongitude;
    }

    /**
     *
     * @return
     * The CollComment
     */
    public String getCollComment() {
        return CollComment;
    }

    /**
     *
     * @param CollComment
     * The CollComment
     */
    public void setCollComment(String CollComment) {
        this.CollComment = CollComment;
    }

    /**
     *
     * @return
     * The DeliveryAddressId
     */
    public String getDeliveryAddressId() {
        return DeliveryAddressId;
    }

    /**
     *
     * @param DeliveryAddressId
     * The DeliveryAddressId
     */
    public void setDeliveryAddressId(String DeliveryAddressId) {
        this.DeliveryAddressId = DeliveryAddressId;
    }

    /**
     *
     * @return
     * The DeliveryPodjed
     */
    public String getDeliveryPodjed() {
        return DeliveryPodjed;
    }

    /**
     *
     * @param DeliveryPodjed
     * The DeliveryPodjed
     */
    public void setDeliveryPodjed(String DeliveryPodjed) {
        this.DeliveryPodjed = DeliveryPodjed;
    }

    /**
     *
     * @return
     * The DeliveryAddrTypeMenu
     */
    public String getDeliveryAddrTypeMenu() {
        return DeliveryAddrTypeMenu;
    }

    /**
     *
     * @param DeliveryAddrTypeMenu
     * The DeliveryAddrTypeMenu
     */
    public void setDeliveryAddrTypeMenu(String DeliveryAddrTypeMenu) {
        this.DeliveryAddrTypeMenu = DeliveryAddrTypeMenu;
    }

    /**
     *
     * @return
     * The DeliveryLandmark
     */
    public String getDeliveryLandmark() {
        return DeliveryLandmark;
    }

    /**
     *
     * @param DeliveryLandmark
     * The DeliveryLandmark
     */
    public void setDeliveryLandmark(String DeliveryLandmark) {
        this.DeliveryLandmark = DeliveryLandmark;
    }

    /**
     *
     * @return
     * The DeliveryTerminal
     */
    public String getDeliveryTerminal() {
        return DeliveryTerminal;
    }

    /**
     *
     * @param DeliveryTerminal
     * The DeliveryTerminal
     */
    public void setDeliveryTerminal(String DeliveryTerminal) {
        this.DeliveryTerminal = DeliveryTerminal;
    }

    /**
     *
     * @return
     * The DeliveryAddressText
     */
    public String getDeliveryAddressText() {
        return DeliveryAddressText;
    }

    /**
     *
     * @param DeliveryAddressText
     * The DeliveryAddressText
     */
    public void setDeliveryAddressText(String DeliveryAddressText) {
        this.DeliveryAddressText = DeliveryAddressText;
    }

    /**
     *
     * @return
     * The DeliveryComment
     */
    public String getDeliveryComment() {
        return DeliveryComment;
    }

    /**
     *
     * @param DeliveryComment
     * The DeliveryComment
     */
    public void setDeliveryComment(String DeliveryComment) {
        this.DeliveryComment = DeliveryComment;
    }

    /**
     *
     * @return
     * The OrderComment
     */
    public String getOrderComment() {
        return OrderComment;
    }

    /**
     *
     * @param OrderComment
     * The OrderComment
     */
    public void setOrderComment(String OrderComment) {
        this.OrderComment = OrderComment;
    }

    /**
     *
     * @return
     * The gType
     */
    public String getGType() {
        return gType;
    }

    /**
     *
     * @param gType
     * The g_type
     */
    public void setGType(String gType) {
        this.gType = gType;
    }

    /**
     *
     * @return
     * The conditioner
     */
    public String getConditioner() {
        return conditioner;
    }

    /**
     *
     * @param conditioner
     * The conditioner
     */
    public void setConditioner(String conditioner) {
        this.conditioner = conditioner;
    }

    /**
     *
     * @return
     * The animal
     */
    public String getAnimal() {
        return animal;
    }

    /**
     *
     * @param animal
     * The animal
     */
    public void setAnimal(String animal) {
        this.animal = animal;
    }

    /**
     *
     * @return
     * The needCheck
     */
    public String getNeedCheck() {
        return needCheck;
    }

    /**
     *
     * @param needCheck
     * The need_check
     */
    public void setNeedCheck(String needCheck) {
        this.needCheck = needCheck;
    }

    /**
     *
     * @return
     * The needWifi
     */
    public String getNeedWifi() {
        return needWifi;
    }

    /**
     *
     * @param needWifi
     * The need_wifi
     */
    public void setNeedWifi(String needWifi) {
        this.needWifi = needWifi;
    }

    /**
     *
     * @return
     * The needCard
     */
    public String getNeedCard() {
        return needCard;
    }

    /**
     *
     * @param needCard
     * The need_card
     */
    public void setNeedCard(String needCard) {
        this.needCard = needCard;
    }

    /**
     *
     * @return
     * The luggage
     */
    public String getLuggage() {
        return luggage;
    }

    /**
     *
     * @param luggage
     * The luggage
     */
    public void setLuggage(String luggage) {
        this.luggage = luggage;
    }

    /**
     *
     * @return
     * The meeting
     */
    public String getMeeting() {
        return meeting;
    }

    /**
     *
     * @param meeting
     * The meeting
     */
    public void setMeeting(String meeting) {
        this.meeting = meeting;
    }

    /**
     *
     * @return
     * The gWidth
     */
    public String getGWidth() {
        return gWidth;
    }

    /**
     *
     * @param gWidth
     * The g_width
     */
    public void setGWidth(String gWidth) {
        this.gWidth = gWidth;
    }

    /**
     *
     * @return
     * The eType
     */
    public String getEType() {
        return eType;
    }

    /**
     *
     * @param eType
     * The e_type
     */
    public void setEType(String eType) {
        this.eType = eType;
    }

    /**
     *
     * @return
     * The OnlinePayment
     */
    public String getOnlinePayment() {
        return OnlinePayment;
    }

    /**
     *
     * @param OnlinePayment
     * The OnlinePayment
     */
    public void setOnlinePayment(String OnlinePayment) {
        this.OnlinePayment = OnlinePayment;
    }

    /**
     *
     * @return
     * The price
     */
    public int getPrice() {
        return price;
    }

    /**
     *
     * @param price
     * The price
     */
    public void setPrice(int price) {
        this.price = price;
    }

    /**
     *
     * @return
     * The NoCashPayment
     */
    public int getNoCashPayment() {
        return NoCashPayment;
    }

    /**
     *
     * @param NoCashPayment
     * The NoCashPayment
     */
    public void setNoCashPayment(int NoCashPayment) {
        this.NoCashPayment = NoCashPayment;
    }

    /**
     *
     * @return
     * The bonus
     */
    public double getBonus() {
        return bonus;
    }

    /**
     *
     * @param bonus
     * The bonus
     */
    public void setBonus(double bonus) {
        this.bonus = bonus;
    }

    /**
     *
     * @return
     * The IdLocality
     */
    public int getIdLocality() {
        return IdLocality;
    }

    /**
     *
     * @param IdLocality
     * The IdLocality
     */
    public void setIdLocality(int IdLocality) {
        this.IdLocality = IdLocality;
    }

    /**
     *
     * @return
     * The triptime
     */
    public int getTriptime() {
        return triptime;
    }

    /**
     *
     * @param triptime
     * The triptime
     */
    public void setTriptime(int triptime) {
        this.triptime = triptime;
    }

    /**
     *
     * @return
     * The cityName
     */
    public String getCityName() {
        return cityName;
    }

    /**
     *
     * @param cityName
     * The cityName
     */
    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    /**
     *
     * @return
     * The tariffName
     */
    public String getTariffName() {
        return tariffName;
    }

    /**
     *
     * @param tariffName
     * The tariffName
     */
    public void setTariffName(String tariffName) {
        this.tariffName = tariffName;
    }

    /**
     *
     * @return
     * The rating
     */
    public int getRating() {
        return rating;
    }

    /**
     *
     * @param rating
     * The rating
     */
    public void setRating(int rating) {
        this.rating = rating;
    }

    /**
     *
     * @return
     * The yellow_reg_num
     */
    public String getYellow_reg_num() {
        return yellow_reg_num;
    }

    /**
     *
     * @param yellow_reg_num
     * The yellow_reg_num
     */
    public void setYellow_reg_num(String yellow_reg_num) {
        this.yellow_reg_num = yellow_reg_num;
    }

    /**
     *
     * @return
     * The hurry
     */
    public int getHurry() {
        return hurry;
    }

    /**
     *
     * @param hurry
     * The hurry
     */
    public void setHurry(int hurry) {
        this.hurry = hurry;
    }

    /**
     *
     * @return
     * The childSeats
     */
    public List<ChildSeatData> getChildSeats() {
        return childSeats;
    }

    /**
     *
     * @param childSeats
     * The child_seats
     */
    public void setChildSeats(List<ChildSeatData> childSeats) {
        this.childSeats = childSeats;
    }

    /**
     *
     * @return
     * The id_card
     */
    public int getId_card() {
        return id_card;
    }

    /**
     *
     * @param id_card
     * The id_card
     */
    public void setId_card(int id_card) {
        this.id_card = id_card;
    }

    /**
     *
     * @return
     * The feedback
     */
    public String getFeedback() {
        return feedback;
    }

    /**
     *
     * @param feedback
     * The feedback
     */
    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(IdOrder);
        dest.writeString(PassengerName);
        dest.writeString(PassengerPhone);
        dest.writeString(CollDate);
        dest.writeString(CollTime);
        dest.writeString(tariff);
        dest.writeInt(NoSmoking);
        dest.writeInt(useBonus);
        dest.writeString(CollAddressId);
        dest.writeString(CollPodjed);
        dest.writeString(status);
        dest.writeString(CollAddrTypeMenu);
        dest.writeString(CollLandmark);
        dest.writeString(CollAddressText);
        dest.writeString(CityFrom);
        dest.writeString(Flight);
        dest.writeString(CollTerminal);
        dest.writeString(MetPhone);
        dest.writeString(Wagon);
        dest.writeString(Train);
        dest.writeString(latitude);
        dest.writeString(longitude);
        dest.writeString(delLatitude);
        dest.writeString(delLongitude);
        dest.writeString(CollComment);
        dest.writeString(DeliveryAddressId);
        dest.writeString(DeliveryPodjed);
        dest.writeString(DeliveryAddrTypeMenu);
        dest.writeString(DeliveryLandmark);
        dest.writeString(DeliveryTerminal);
        dest.writeString(DeliveryAddressText);
        dest.writeString(DeliveryComment);
        dest.writeString(OrderComment);
        dest.writeString(gType);
        dest.writeString(conditioner);
        dest.writeString(animal);
        dest.writeString(needCheck);
        dest.writeString(needWifi);
        dest.writeString(needCard);
        dest.writeString(luggage);
        dest.writeString(meeting);
        dest.writeString(gWidth);
        dest.writeString(eType);
        dest.writeString(OnlinePayment);
        dest.writeInt(price);
        dest.writeInt(NoCashPayment);
        dest.writeDouble(bonus);
        dest.writeInt(IdLocality);
        dest.writeInt(triptime);
        dest.writeString(cityName);
        dest.writeString(tariffName);
        dest.writeInt(rating);
        dest.writeString(yellow_reg_num);
        dest.writeInt(hurry);
        dest.writeTypedList(childSeats);
        dest.writeInt(id_card);
        dest.writeString(feedback);
    }
}


