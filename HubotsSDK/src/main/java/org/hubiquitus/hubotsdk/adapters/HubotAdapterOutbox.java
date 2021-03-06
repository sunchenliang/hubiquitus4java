/*
 * Copyright (c) Novedia Group 2012.
 *
 *    This file is part of Hubiquitus
 *
 *    Permission is hereby granted, free of charge, to any person obtaining a copy
 *    of this software and associated documentation files (the "Software"), to deal
 *    in the Software without restriction, including without limitation the rights
 *    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 *    of the Software, and to permit persons to whom the Software is furnished to do so,
 *    subject to the following conditions:
 *
 *    The above copyright notice and this permission notice shall be included in all copies
 *    or substantial portions of the Software.
 *
 *    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 *    INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 *    PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 *    FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 *    ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *    You should have received a copy of the MIT License along with Hubiquitus.
 *    If not, see <http://opensource.org/licenses/mit-license.php>.
 */


package org.hubiquitus.hubotsdk.adapters;

import org.hubiquitus.hapi.client.HMessageDelegate;
import org.hubiquitus.hapi.hStructures.HMessage;
import org.hubiquitus.hubotsdk.AdapterOutbox;
import org.hubiquitus.hubotsdk.HubotMessageStructure;
import org.hubiquitus.hubotsdk.ProducerTemplateSingleton;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HubotAdapterOutbox extends AdapterOutbox {

	final Logger logger = LoggerFactory.getLogger(HubotAdapterOutbox.class);

	public HubotAdapterOutbox() {
		super();
	}

	@Override
	public void setProperties(JSONObject properties) {
		// nop
		
	}

	@Override
	public void start() {
		// nop
	}

	@Override
	public void stop() {
		// nop
		
	}

	@Override
	public void sendMessage(HMessage message, final HMessageDelegate callback) {

            if (callback != null) {
                    hclient.send(message, new HMessageDelegate() {
                    @Override
                    public void onMessage(HMessage message) {
                        ProducerTemplateSingleton.getProducerTemplate().sendBody("seda:inbox",new HubotMessageStructure(message, callback));
                    }
                });
            } else {
                hclient.send(message, null);
            }
	}

}
