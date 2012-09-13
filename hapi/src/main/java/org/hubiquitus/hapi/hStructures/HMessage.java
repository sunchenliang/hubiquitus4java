/*
 * Copyright (c) Novedia Group 2012.
 *
 *     This file is part of Hubiquitus.
 *
 *     Hubiquitus is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Hubiquitus is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Hubiquitus.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.hubiquitus.hapi.hStructures;

import java.util.Calendar;
import org.hubiquitus.hapi.util.DateISO8601;
import org.hubiquitus.hapi.util.HJsonDictionnary;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @version 0.5 hAPI Command. For more info, see Hubiquitus reference
 */

public class HMessage implements HJsonObj {

	private JSONObject hmessage = new JSONObject();

	public HMessage() {
	}

	public HMessage(JSONObject jsonObj) {
		fromJSON(jsonObj);
	}

	/* HJsonObj interface */

	public JSONObject toJSON() {
		return this.hmessage;
	}

	public void fromJSON(JSONObject jsonObj) {
		if (jsonObj != null) {
			this.hmessage = jsonObj;
		} else {
			this.hmessage = new JSONObject();
		}
	}

	public String getHType() {
		return "hmessage";
	}

	@Override
	public String toString() {
		return hmessage.toString();
	}

	/**
	 * Check are made on : msgid, chid, convid, ref, type, priority, relevance,
	 * transient, author, publisher, published, timeout and location.
	 * 
	 * @param HAck
	 * @return Boolean
	 */
	public boolean equals(HMessage obj) {
		if (obj.getMsgid() != this.getMsgid())
			return false;
		if (obj.getActor() != this.getActor())
			return false;
		if (obj.getConvid() != this.getConvid())
			return false;
		if (obj.getRef() != this.getRef())
			return false;
		if (obj.getType() != this.getType())
			return false;
		if (obj.getPriority().value() != this.getPriority().value())
			return false;
		if (obj.getRelevance() != this.getRelevance())
			return false;
		if (obj.getPersistent() != this.getPersistent())
			return false;
		if (obj.getAuthor() != this.getAuthor())
			return false;
		if (obj.getPublisher() != this.getPublisher())
			return false;
		if (obj.getPublished() != this.getPublished())
			return false;
		if (obj.getTimeout() != this.getTimeout())
			return false;
		if (obj.getLocation().equals(this.getLocation()))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		return hmessage.hashCode();
	}

	/* Getters & Setters */

	/**
	 * Mandatory. Filled by the hApi.
	 * 
	 * @return message id. NULL if undefined
	 */
	public String getMsgid() {
		String msgid;
		try {
			msgid = hmessage.getString("msgid");
		} catch (Exception e) {
			msgid = null;
		}
		return msgid;
	}

	public void setMsgid(String msgid) {
		try {
			if (msgid == null) {
				hmessage.remove("msgid");
			} else {
				hmessage.put("msgid", msgid);
			}
		} catch (JSONException e) {
		}
	}

	/**
	 * Mandatory The unique ID of the channel through which the message is
	 * published.
	 * 
	 * The JID through which the message is published. The JID can be that of a
	 * channel (beginning with #) or a user.
	 * 
	 * A special actor called ‘session’ indicates that the HServer should handle
	 * the hMessage.
	 * 
	 * @return actor. NULL if undefined
	 */
	public String getActor() {
		String actor;
		try {
			actor = hmessage.getString("actor");
		} catch (Exception e) {
			actor = null;
		}
		return actor;
	}

	public void setActor(String actor) {
		try {
			if (actor == null) {
				hmessage.remove("actor");
			} else {
				hmessage.put("actor", actor);
			}
		} catch (JSONException e) {
		}
	}

	/**
	 * Mandatory. Filled by the hApi if empty.
	 * 
	 * @return conversation id. NULL if undefined
	 */
	public String getConvid() {
		String convid;
		try {
			convid = hmessage.getString("convid");
		} catch (Exception e) {
			convid = null;
		}
		return convid;
	}

	public void setConvid(String convid) {
		try {
			if (convid == null) {
				hmessage.remove("convid");
			} else {
				hmessage.put("convid", convid);
			}
		} catch (JSONException e) {
		}
	}

	/**
	 * Since v0.5
	 * 
	 * @return reference to another hMessage msgid. NULL if undefined.
	 */
	public String getRef() {
		String ref;
		try {
			ref = hmessage.getString("ref");
		} catch (Exception e) {
			ref = null;
		}
		return ref;
	}

	/**
	 * Since v0.5 Refers to another hMessage msgid. Provide a mechanism to do
	 * correlation between messages. For example, it is used by the command
	 * pattern and the acknowledgement (see. hAck)
	 * 
	 * @param ref
	 */
	public void setRef(String ref) {
		try {
			if (ref == null) {
				hmessage.remove("ref");
			} else {
				hmessage.put("ref", ref);
			}
		} catch (JSONException e) {
		}

	}

	/**
	 * @return type of the message payload. NULL if undefined
	 */
	public String getType() {
		String type;
		try {
			type = hmessage.getString("type");
		} catch (Exception e) {
			type = null;
		}
		return type;
	}

	public void setType(String type) {
		try {
			if (type == null) {
				hmessage.remove("type");
			} else {
				hmessage.put("type", type);
			}
		} catch (JSONException e) {
		}
	}

	/**
	 * If UNDEFINED, priority lower to 0.
	 * 
	 * @return priority.
	 */
	public HMessagePriority getPriority() {
		HMessagePriority priority;
		try {
			int priorityInt = hmessage.getInt("priority");
			if (priorityInt < 0 || priorityInt > 5) {
				priority = null;
			} else {
				priority = HMessagePriority.constant(priorityInt);
			}
		} catch (Exception e1) {
			priority = null;
		}
		return priority;
	}

	public void setPriority(HMessagePriority priority) {
		try {
			if (priority == null) {
				hmessage.remove("priority");
			} else {
				hmessage.put("priority", priority.value());
			}
		} catch (JSONException e) {
		}
	}

	/**
	 * Date-time until which the message is considered as relevant.
	 * 
	 * @return relevance. NULL if undefined
	 */
	public Calendar getRelevance() {
		Calendar relevance;
		try {
			relevance = (DateISO8601
					.toCalendar(hmessage.getString("relevance")));
			;
		} catch (JSONException e) {
			relevance = null;
		}
		return relevance;
	}

	public void setRelevance(Calendar relevance) {
		try {
			if (relevance == null) {
				hmessage.remove("relevance");
			} else {
				hmessage.put("relevance", DateISO8601.fromCalendar(relevance));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Since v0.5
	 * 
	 * @return persist message or not. NULL if undefined
	 */
	public Boolean getPersistent() {
		Boolean _persistent;
		try {
			_persistent = hmessage.getBoolean("persistent");
		} catch (JSONException e) {
			_persistent = null;
		}
		return _persistent;
	}

	/**
	 * Since v0.5 Possible values are : true : indicates if the message MUST be
	 * persisted by the middleware false: indicates that the message is volatile
	 * and will not be persisted by the middleware. Defaults to true if omitted.
	 * 
	 * @param _persistent
	 */
	public void setPersistent(Boolean _persistent) {
		try {
			if (_persistent == null) {
				hmessage.remove("persistent");
			} else {
				hmessage.put("persistent", _persistent);
			}
		} catch (JSONException e) {
		}
	}

	/**
	 * The geographical location to which the message refer.
	 * 
	 * @return location. NULL if undefined
	 */
	public HLocation getLocation() {
		HLocation location;
		try {
			location = new HLocation(hmessage.getJSONObject("location"));
		} catch (JSONException e) {
			location = null;
		}
		return location;
	}

	public void setLocation(HLocation location) {
		try {
			if (location == null) {
				hmessage.remove("location");
			} else {
				hmessage.put("location", location.toJSON());
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return author of this message. NULL if undefined
	 */
	public String getAuthor() {
		String author;
		try {
			author = hmessage.getString("author");
		} catch (Exception e) {
			author = null;
		}
		return author;
	}

	public void setAuthor(String author) {
		try {
			if (author == null) {
				hmessage.remove("author");
			} else {
				hmessage.put("author", author);
			}
		} catch (JSONException e) {
		}
	}

	/**
	 * Mandatory
	 * 
	 * @return publisher of this message. NULL if undefined
	 */
	public String getPublisher() {
		String publisher;
		try {
			publisher = hmessage.getString("publisher");
		} catch (Exception e) {
			publisher = null;
		}
		return publisher;
	}

	public void setPublisher(String publisher) {
		try {
			if (publisher == null) {
				hmessage.remove("publisher");
			} else {
				hmessage.put("publisher", publisher);
			}
		} catch (JSONException e) {
		}
	}

	/**
	 * Mandatory. The date and time at which the message has been published.
	 * 
	 * @return published. NULL if undefined
	 */
	public Calendar getPublished() {
		Calendar published;
		try {
			published = (DateISO8601
					.toCalendar(hmessage.getString("published")));
		} catch (JSONException e) {
			published = null;
		}
		return published;
	}

	public void setPublished(Calendar published) {
		try {
			if (published == null) {
				hmessage.remove("published");
			} else {
				hmessage.put("published", DateISO8601.fromCalendar(published));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * The list of headers attached to this message.
	 * 
	 * @return Headers. NULL if undefined
	 */
	public HJsonObj getHeaders() {
		HJsonDictionnary headers = new HJsonDictionnary();
		try {
			headers.fromJSON(hmessage.getJSONObject("headers"));
		} catch (JSONException e) {
			headers = null;
		}
		return headers;
	}

	public void setHeaders(HJsonObj headers) {
		try {
			if (headers == null) {
				hmessage.remove("headers");
			} else {
				hmessage.put("headers", headers.toJSON());
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * The content of the message.
	 * 
	 * @return payload. NULL if undefined
	 */
	public HJsonObj getPayload() {
		HJsonObj payload;
		try {
			JSONObject jsonPayload = hmessage.getJSONObject("payload");
			String type = this.getType().toLowerCase();
			if (type.equalsIgnoreCase("hmeasure")) {
				payload = new HMeasure(jsonPayload);
			} else if (type.equalsIgnoreCase("halert")) {
				payload = new HAlert(jsonPayload);
			} else if (type.equalsIgnoreCase("hack")) {
				payload = new HAck(jsonPayload);
			} else if (type.equalsIgnoreCase("hconvstate")) {
				payload = new HConvState(jsonPayload);
			} else {
				payload = new HJsonDictionnary(jsonPayload);
			}
		} catch (JSONException e) {
			payload = null;
		}
		return payload;
	}

	public void setPayload(HJsonObj payload) {
		try {
			if (payload == null) {
				hmessage.remove("payload");
			} else {
				hmessage.put("payload", payload.toJSON());
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Since v0.5
	 * 
	 * @return timeout. 0 if undefined.
	 */
	public int getTimeout() {
		int timeout;
		try {
			timeout = hmessage.getInt("timeout");
		} catch (Exception e) {
			timeout = 0;
		}
		return timeout;
	}

	/**
	 * Since v0.5 Define the timeout in ms to get an answer to the hMessage.
	 * 
	 * @param timeout
	 */
	public void setTimeout(int timeout) {
		try {
			if (timeout == 0) {
				hmessage.remove("timeout");
			} else {
				hmessage.put("timeout", timeout);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}