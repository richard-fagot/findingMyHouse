package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import play.db.ebean.Model;

@SuppressWarnings("serial")
@Entity
public class SmallAds extends Model {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	
	String url;
	
	@ManyToOne
	Distance distance;
	
	@Transient
	private Date date;
	
	private double surface;
	
	private Double price;
	
	private boolean isAccepted;

	public boolean isAccepted() {
		return isAccepted;
	}

	public void setAccepted(boolean isAccepted) {
		this.isAccepted = isAccepted;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Distance getDistance() {
		return distance;
	}

	public void setDistance(Distance distance) {
		this.distance = distance;
	}
	
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void setSurface(double surface) {
		this.surface = surface;
	}
	
	public double getSurface() {
		return this.surface;
	}
	
	public Double getPrice() {
		return price;
	}
	
	public void setPrice(Double price) {
		this.price = price;
	}

	public static Finder<Long, SmallAds> find = new Finder<Long, SmallAds>(Long.class, SmallAds.class);

	
}
