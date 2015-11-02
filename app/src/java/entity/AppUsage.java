package entity;

import dao.Utility;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class AppUsage implements Comparable<AppUsage>{

	private String timestamp;
	private String macAddress;
	private int appId;
        private App app;

	public AppUsage(String timestamp, String macAddress, int appId) {
		this.timestamp = timestamp;
		this.macAddress = macAddress;
		this.appId = appId;
	}
        
        public AppUsage(String timestamp, String macAddress, int appId, App app) {
		this.timestamp = timestamp;
		this.macAddress = macAddress;
		this.app = app;
	}

        public void setApp(App app) {
            this.app = app;
        }

        public App getApp() {
            return app;
        }

	public String getTimestamp() {
		return timestamp;
	}

	public Date getDate() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = dateFormat.parse(timestamp, new ParsePosition(0));
		return date;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public int getAppId() {
		return appId;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	public void setAppId(int appId) {
		this.appId = appId;
	}

        @Override
        public int compareTo(AppUsage o) {
            if(Utility.parseDate(timestamp).after(Utility.parseDate(o.timestamp))){
                return -1;
            }else if(Utility.parseDate(timestamp).before(Utility.parseDate(o.timestamp))){
                return 1;
            }else{
                return 0;
            }
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 11 * hash + Objects.hashCode(this.app);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final AppUsage other = (AppUsage) obj;
            if (!Objects.equals(this.app, other.app)) {
                return false;
            }
            return true;
        }
        
}
