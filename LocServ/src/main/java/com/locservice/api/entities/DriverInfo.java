package com.locservice.api.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vahagn Gevorgyan
 * 02 March 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class DriverInfo extends APIError implements Parcelable {

    @SerializedName("Id")
    @Expose
    private String Id;
    @SerializedName("Name")
    @Expose
    private String Name;
    @SerializedName("CarMark")
    @Expose
    private String CarMark;
    @SerializedName("CarModel")
    @Expose
    private String CarModel;
    @SerializedName("CarColor")
    @Expose
    private String CarColor;
    @SerializedName("CarColorCode")
    @Expose
    private String CarColorCode;
    @SerializedName("CarRegNum")
    @Expose
    private String CarRegNum;
    @SerializedName("License")
    @Expose
    private String License;
    @SerializedName("CarNumber")
    @Expose
    private String CarNumber;
    @SerializedName("Phone")
    @Expose
    private String Phone;
    @SerializedName("PhotoLink")
    @Expose
    private String PhotoLink;
    @SerializedName("Rate")
    @Expose
    private double Rate;
    @SerializedName("RatePlace")
    @Expose
    private int RatePlace;
    @SerializedName("OrdersCount")
    @Expose
    private int OrdersCount;
    @SerializedName("RateByClient")
    @Expose
    private double RateByClient;
    @SerializedName("Feedbacks")
    @Expose
    private List<DriverInfoFeedback> Feedbacks = new ArrayList<DriverInfoFeedback>();
    @SerializedName("Orders")
    @Expose
    private List<DriverInfoOrder> Orders = new ArrayList<DriverInfoOrder>();
    @SerializedName("car_type")
    @Expose
    private String carType;

    protected DriverInfo(Parcel in) {
        Id = in.readString();
        Name = in.readString();
        CarMark = in.readString();
        CarModel = in.readString();
        CarColor = in.readString();
        CarColorCode = in.readString();
        CarRegNum = in.readString();
        License = in.readString();
        CarNumber = in.readString();
        Phone = in.readString();
        PhotoLink = in.readString();
        Rate = in.readDouble();
        RatePlace = in.readInt();
        OrdersCount = in.readInt();
        RateByClient = in.readDouble();
        Feedbacks = in.createTypedArrayList(DriverInfoFeedback.CREATOR);
        Orders = in.createTypedArrayList(DriverInfoOrder.CREATOR);
        carType = in.readString();
    }

    public static final Creator<DriverInfo> CREATOR = new Creator<DriverInfo>() {
        @Override
        public DriverInfo createFromParcel(Parcel in) {
            return new DriverInfo(in);
        }

        @Override
        public DriverInfo[] newArray(int size) {
            return new DriverInfo[size];
        }
    };

    /**
     *
     * @return
     * The Id
     */
    public String getId() {
        return Id;
    }

    /**
     *
     * @param Id
     * The Id
     */
    public void setId(String Id) {
        this.Id = Id;
    }

    /**
     *
     * @return
     * The Name
     */
    public String getName() {
        return Name;
    }

    /**
     *
     * @param Name
     * The Name
     */
    public void setName(String Name) {
        this.Name = Name;
    }

    /**
     *
     * @return
     * The CarMark
     */
    public String getCarMark() {
        return CarMark;
    }

    /**
     *
     * @param CarMark
     * The CarMark
     */
    public void setCarMark(String CarMark) {
        this.CarMark = CarMark;
    }

    /**
     *
     * @return
     * The CarModel
     */
    public String getCarModel() {
        return CarModel;
    }

    /**
     *
     * @param CarModel
     * The CarModel
     */
    public void setCarModel(String CarModel) {
        this.CarModel = CarModel;
    }

    /**
     *
     * @return
     * The CarColor
     */
    public String getCarColor() {
        return CarColor;
    }

    /**
     *
     * @param CarColor
     * The CarColor
     */
    public void setCarColor(String CarColor) {
        this.CarColor = CarColor;
    }

    /**
     *
     * @return
     * The CarRegNum
     */
    public String getCarRegNum() {
        return CarRegNum;
    }

    /**
     *
     * @param CarRegNum
     * The CarRegNum
     */
    public void setCarRegNum(String CarRegNum) {
        this.CarRegNum = CarRegNum;
    }

    /**
     *
     * @return
     * The CarColorCode
     */
    public String getCarColorCode() {
        return CarColorCode;
    }

    /**
     *
     * @param CarColorCode
     * The CarColorCode
     */
    public void setCarColorCode(String CarColorCode) {
        this.CarColorCode = CarColorCode;
    }

    /**
     *
     * @return
     * The License
     */
    public String getLicense() {
        return License;
    }

    /**
     *
     * @param License
     * The License
     */
    public void setLicense(String License) {
        this.License = License;
    }

    /**
     *
     * @return
     * The CarNumber
     */
    public String getCarNumber() {
        return CarNumber;
    }

    /**
     *
     * @param CarNumber
     * The CarNumber
     */
    public void setCarNumber(String CarNumber) {
        this.CarNumber = CarNumber;
    }

    /**
     *
     * @return
     * The Phone
     */
    public String getPhone() {
        return Phone;
    }

    /**
     *
     * @param Phone
     * The Phone
     */
    public void setPhone(String Phone) {
        this.Phone = Phone;
    }

    /**
     *
     * @return
     * The PhotoLink
     */
    public String getPhotoLink() {
        return PhotoLink;
    }

    /**
     *
     * @param PhotoLink
     * The PhotoLink
     */
    public void setPhotoLink(String PhotoLink) {
        this.PhotoLink = PhotoLink;
    }

    /**
     *
     * @return
     * The Rate
     */
    public double getRate() {
        return Rate;
    }

    /**
     *
     * @param Rate
     * The Rate
     */
    public void setRate(double Rate) {
        this.Rate = Rate;
    }

    /**
     *
     * @return
     * The RatePlace
     */
    public int getRatePlace() {
        return RatePlace;
    }

    /**
     *
     * @param RatePlace
     * The RatePlace
     */
    public void setRatePlace(int RatePlace) {
        this.RatePlace = RatePlace;
    }

    /**
     *
     * @return
     * The OrdersCount
     */
    public int getOrdersCount() {
        return OrdersCount;
    }

    /**
     *
     * @param OrdersCount
     * The OrdersCount
     */
    public void setOrdersCount(int OrdersCount) {
        this.OrdersCount = OrdersCount;
    }

    /**
     *
     * @return
     * The RateByClient
     */
    public double getRateByClient() {
        return RateByClient;
    }

    /**
     *
     * @param RateByClient
     * The RateByClient
     */
    public void setRateByClient(double RateByClient) {
        this.RateByClient = RateByClient;
    }

    /**
     *
     * @return
     * The Feedbacks
     */
    public List<DriverInfoFeedback> getFeedbacks() {
        return Feedbacks;
    }

    /**
     *
     * @param Feedbacks
     * The Feedbacks
     */
    public void setFeedbacks(List<DriverInfoFeedback> Feedbacks) {
        this.Feedbacks = Feedbacks;
    }

    /**
     *
     * @return
     * The Orders
     */
    public List<DriverInfoOrder> getOrders() {
        return Orders;
    }

    /**
     *
     * @param Orders
     * The Orders
     */
    public void setOrders(List<DriverInfoOrder> Orders) {
        this.Orders = Orders;
    }

    /**
     *
     * @param carType
     * The car Type
     */
    public String getCarType() {
        return carType;
    }

    /**
     *
     * @param carType
     * The car Type
     */
    public void setCarType(String carType) {
        this.carType = carType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Id);
        dest.writeString(Name);
        dest.writeString(CarMark);
        dest.writeString(CarModel);
        dest.writeString(CarColor);
        dest.writeString(CarColorCode);
        dest.writeString(CarRegNum);
        dest.writeString(License);
        dest.writeString(CarNumber);
        dest.writeString(Phone);
        dest.writeString(PhotoLink);
        dest.writeDouble(Rate);
        dest.writeInt(RatePlace);
        dest.writeInt(OrdersCount);
        dest.writeDouble(RateByClient);
        dest.writeTypedList(Feedbacks);
        dest.writeTypedList(Orders);
        dest.writeString(carType);
    }
}
