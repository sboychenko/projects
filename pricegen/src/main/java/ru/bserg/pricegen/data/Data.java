package ru.bserg.pricegen.data;

public class Data {
	
	private int num;
	private String brand;
	private String group;
	private Long code;
	private String name;
	private String sku;
	private Double price;
	private Boolean bool; 
	private Double doub;
	
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public Long getCode() {
		return code;
	}
	public void setCode(Long code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSku() {
		return sku;
	}
	public void setSku(String sku) {
		this.sku = sku;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public Boolean getBool() {
		return bool;
	}
	public void setBool(Boolean bool) {
		this.bool = bool;
	}
	public Double getDoub() {
		return doub;
	}
	public void setDoub(Double doub) {
		this.doub = doub;
	}
	
	@Override
	public String toString() {
		return "DATA [num="+num+"; brand="+brand+"; group="+group+"; code="+ code+"; name="+name+"; sku="+sku+"; price="+price+"; bool="+bool+"; doub="+doub+"]";
	}
}
