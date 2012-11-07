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

package org.hubiquitus.twitter4j.stream.pub;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

public class HUserStream {

	private String proxyHost;
	private int proxyPort;
	private String tags;
	private String consumerKey;
	private String consumerSecret;	    
	private String token;
	private String tokenSecret;

	private String delimited;
	private String stallWarnings; 
	private String with;
	private String replies;
	private String locations;
	private String count;
	/**
	 * The map where the JSONObject's properties are kept.
	 */
	@SuppressWarnings("rawtypes")
	private Map map;
	
	/**
	 * Get an optional value associated with a key.
	 *
	 * @param key A key string.
	 * @return An object which is the value, or null if there is no value.
	 */
	public Object opt(String key) {
		return key == null ? null : this.map.get(key);
	}

	/**
	 * Determine if the value associated with the key is null or if there is
	 * no value.
	 *
	 * @param key A key string.
	 * @return true if there is no value associated with the key or if
	 *         the value is the JSONObject.NULL object.
	 */
	public boolean isNull(String key) {
		return JSONObject.NULL.equals(opt(key));
	}

	
	private class GZipStream extends GZIPInputStream {
		private final InputStream wrapped;
		public GZipStream(InputStream is) throws IOException {
			super(is);
			wrapped = is;
		}

		/**
		 * Overrides behavior of GZIPInputStream which assumes we have all the data available
		 * which is not true for streaming. We instead rely on the underlying stream to tell us
		 * how much data is available.
		 * 
		 * Programs should not count on this method to return the actual number
		 * of bytes that could be read without blocking.
		 *
		 * @return - whatever the wrapped InputStream returns
		 * @exception  IOException  if an I/O error occurs.
		 */
		public int available() throws IOException {
			return wrapped.available();
		}
	}

	private class Loop extends Thread {

		private Logger log = Logger.getLogger(HUserStream.class);
		private HUserStream owner;

		Loop(HUserStream owner) {
			super("Thread used to read the Twitter User Streaming API v1.1");
			this.owner = owner;
		}

		BufferedReader buffer;

		public void run() {
			while (true) {
				try {
					String line = buffer.readLine();
					try {
						if ((line != null) && (line.startsWith("{"))) { 
							JSONObject object = new JSONObject(line);
							owner.fireEvent(object);
						}
					} catch (JSONException e) {
						log.error("ooops, there is some trouble with the JSON Parsing of a line from twitter :(", e);
					}
				} catch (IOException e) {
					if (!isInterrupted())
						log.error("can not read a line :(", e);
					else {
						try {
							buffer.close();
						} catch (IOException e1) {
							log.error("ooops can not close properly the buffer...", e1);
						}
						break;
					}
				}
			}
		}
	}

	private Loop loop = new Loop(this);

	private static final String USERSTREAM_API_1_1_ENDPOINT = "https://userstream.twitter.com/1.1/user.json";

	private static Logger log = Logger.getLogger(HUserStream.class);

	private ArrayList<HUserStreamListner> listeners = new ArrayList<HUserStreamListner>();

	public HUserStream(String proxyHost, int proxyPort, String tags,String delimited,String stallWarnings, String with, String replies, String locations,String count,String consumerKey, String consumerSecret, String token, String tokenSecret) {
		super();
		this.proxyHost = proxyHost;
		this.proxyPort = proxyPort;
		
		this.tags = tags;
		this.delimited =delimited;
		this.stallWarnings = stallWarnings; 
		this.with = with;
		this.replies = replies;
		this.locations = locations;
		this.count = count;

		this.consumerKey = consumerKey;
		this.consumerSecret = consumerSecret;
		this.token = token;
		this.tokenSecret = tokenSecret;
	}

	/**
	 * Used to add a listener
	 * @param listener which must implement the HStreamlistener to get the status
	 */
	public void addListener(HUserStreamListner listener) {
		listeners.add(listener);
		log.debug("listener added: "+listener);
	}

	public void removeListener(HUserStreamListner listener) {
		listeners.remove(listener);
		log.debug("listener removed: "+listener);
	}

	public void fireEvent(JSONObject item) {  	    
		for (HUserStreamListner listener : listeners) {
			if (item.has("text"))
				listener.onStatus(item);
			else if (item.has("warning"))
				listener.onStallWarning(item);
			else if (item.has("delete"))
				listener.onLocationDeletionNotices(item);
			else if (item.has("scrub_geo"))
				listener.onLimitNotices(item);
			else if (item.has("limit"))
				listener.onStatusDeletionNotices(item);    		
			else if (item.has("status_withheld"))
				listener.onStatusWithheld(item);
			else if (item.has("user_withheld"))
				listener.onUserWithheld(item);
			else if (item.has("disconnect")){			
				listener.onDisconnectMessages(item);
				//-----  RECONNECTING ----
				log.info("RECONNECTING ...");
				
				HUserStream stream = new HUserStream (
						proxyHost, 
						proxyPort, 
						tags, 
						delimited,
						stallWarnings, 
						with,
						replies,
						locations,
						count,
						consumerKey, 
						consumerSecret, 
						token, 
						tokenSecret);
				stream.addListener(listener);

				try {
					log.info("ITEM  :"+item + "Code :"+item.getJSONObject("disconnect").getInt("code"));
					if (item.getJSONObject("disconnect").getInt("code") == 7){
						try {
							Thread.sleep(9000);
							stream.start();
						} catch (InterruptedException e) {
							log.error("Error Thread donst sleep correctelly "+e);					
						}
					}

				} catch (JSONException e1) {
					log.error("Error in get code :"+e1);
					e1.printStackTrace();
				}
				//------
			}
			else 
				listener.onOtherMessage(item);
		}
	}

	public void start(){
		stop();
		String url = USERSTREAM_API_1_1_ENDPOINT;

		DefaultHttpClient client = new DefaultHttpClient();	

		if ((proxyHost != null) && (proxyPort > 0)) {
			HttpHost proxy = new HttpHost(proxyHost, proxyPort, "http");
			client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
			log.debug("using a proxy : " + proxyHost+":"+proxyPort);
		}

		HttpPost post = new HttpPost(url);
		/**
		 * Post Parameters 
		 * */
		ArrayList<BasicNameValuePair> nValuePairs = new ArrayList <BasicNameValuePair>();		
		
		
		
		if( stallWarnings!=null){
			nValuePairs.add(new BasicNameValuePair("stall_warnings", stallWarnings));	
		}
		if( tags!=null){
			nValuePairs.add(new BasicNameValuePair("track", tags));	
		}
		if( delimited != null){
			nValuePairs.add(new BasicNameValuePair("delimited", delimited));
		}
		if( with != null){
			nValuePairs.add(new BasicNameValuePair("with", with));
		}
		if( replies!=null){
			nValuePairs.add(new BasicNameValuePair("replies", replies));
		}
		if( locations!=null){
			nValuePairs.add(new BasicNameValuePair("locations", locations));
		}
		if( count!=null){
			nValuePairs.add(new BasicNameValuePair("count", count));
		}
		
		
		try {
			post.setEntity(new UrlEncodedFormEntity(nValuePairs));
		} catch (UnsupportedEncodingException e1) {
			log.debug(" Error on setEntity :",e1);		
		}		

		log.debug("post : "+post + "Entity = "+post.getEntity().toString());		

		//--------  Ask the twitter stream  API the gzip stream ------	
		post.setHeader("Accept-Encoding", "deflate, gzip"); 
		log.trace("using deflate, gzip");

		//--------  Sign a Post with oauth using oauth.signpost ------	  
		CommonsHttpOAuthConsumer consumer = new CommonsHttpOAuthConsumer(consumerKey, consumerSecret);
		consumer.setTokenWithSecret(token, tokenSecret);
		log.trace("consumer OAuth created");

		try {
			consumer.sign(post);
			log.debug("consumer OAuth signed");
		} catch (OAuthMessageSignerException e) {
			log.error("ERROR AT OAUTH, ",e);			
		} catch (OAuthExpectationFailedException e) {
			log.error("ERROR AT OAUTH, ",e);			
		} catch (OAuthCommunicationException e) {
			log.error("ERROR AT OAUTH, ",e);			
		}	    
		//--------------------------------------------------------------

		HttpResponse response;
		try {
			response = client.execute(post);
			log.trace("post executed");
			HttpEntity entity = response.getEntity();
			log.trace("HttpEntity fetched");			
			if (entity == null) throw new IOException("No entity");
			log.trace("HttpEntity not null :)");
			loop.buffer = new BufferedReader(new InputStreamReader(new GZipStream( entity.getContent() ), "UTF-8"));
			log.trace("Buffer created");
			loop.start();
			log.debug("Stream started");
		} catch (ClientProtocolException e) {
			log.error("Error during execution, ",e);			
		} catch (IOException e) {
			log.error("Error during execution, ",e);			
		}	    
	}

	public void stop(){  
		if (!loop.isAlive() || !loop.isInterrupted())
			loop.interrupt();    	
		log.debug("Stream stopped");
	} 

}
