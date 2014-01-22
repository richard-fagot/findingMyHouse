package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.db.ebean.Model;

@SuppressWarnings("serial")
@Entity
public class LastCall extends Model {
	@Id
	Date date;
	
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public static Finder<Long, LastCall> find = new Finder<Long, LastCall>(Long.class, LastCall.class);
}
