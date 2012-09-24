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

import org.apache.log4j.Logger;
import org.hubiquitus.hapi.hStructures.HJsonObj;
import org.hubiquitus.hapi.hStructures.HMessage;
import org.json.JSONException;

public abstract class AdapterOutbox extends Adapter {
	
	private static Logger logger = Logger.getLogger(AdapterOutbox.class);
	
	// Method for output message and command 
	public final void onOutGoing(HJsonObj hjson) {
        try {
			sendMessage(new HMessage(hjson.toJSON()));
        } catch (JSONException e) {
            logger.error("can not convert the JSON to hMessage", e);
        }
	}

	public abstract void sendMessage(HMessage message);

}
