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

package org.hubiquitus.hubotsdk;

import java.util.Map;

import org.apache.camel.impl.DefaultCamelContext;
import org.hubiquitus.hapi.client.HClient;
import org.hubiquitus.hapi.client.HMessageDelegate;
import org.hubiquitus.hapi.hStructures.HMessage;
import org.hubiquitus.hapi.hStructures.ResultStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public abstract class Adapter implements HMessageDelegate{
	final Logger logger = LoggerFactory.getLogger(Adapter.class);
	
	protected HClient hclient;
	protected String name;
	protected DefaultCamelContext camelContext;
	
	/**
	 * Method used to set properties of the adapters. 
	 * SHOULD BE OVERWRITE
	 * @param params
	 */
	public abstract void setProperties(Map<String,String> params);

	/**
	 *  Method to start the bot
	 */
	public abstract void start();

	/**
	 * Method to stop the bot
	 */
	public abstract void stop();
	
	
	public final void setCamelContext(DefaultCamelContext camelContext) {
		this.camelContext = camelContext;				
	}
	
	public final void setHClient(HClient hclient) {
		this.hclient = hclient;				
	}
	
	public final void setName(String name) {
		this.name = name;				
	}
	
	public final String getName() {
		return name;
	}
	
	
	/**
	 * Allow the user to update the properties of the adapter. During this update, this adapter is stop.
	 * @param params
	 */
	public final void updateProperties(Map<String,String> params) {
		stop();
		setProperties(params);
		start();
	}
	
	public void onMessage(HMessage message) {
		if(message.getPayloadAsHResult().getStatus() != ResultStatus.NO_ERROR) 
			logger.error(("Erreur lors de la commande : " + message.getPayloadAsHResult()));
	}
}
