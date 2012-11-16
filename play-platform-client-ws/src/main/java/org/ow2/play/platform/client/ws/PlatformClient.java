/**
 *
 * Copyright (c) 2012, PetalsLink
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA 
 *
 */
package org.ow2.play.platform.client.ws;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ow2.play.platform.api.API;
import org.ow2.play.platform.api.APIException;
import org.ow2.play.platform.api.Constants;
import org.ow2.play.platform.api.SubscriptionService;
import org.ow2.play.platform.api.bean.Property;
import org.ow2.play.platform.api.bean.Subscription;
import org.ow2.play.platform.api.bean.SubscriptionResult;
import org.ow2.play.platform.client.api.ClientException;
import org.petalslink.dsb.cxf.CXFHelper;

/**
 * Platform client. Check
 * http://www.benmccann.com/dev-blog/apache-cxf-tutorial-ws
 * -security-with-spring/ for future security.
 * 
 * @author chamerling
 * 
 */
public class PlatformClient implements
		org.ow2.play.platform.client.api.PlatformClient {

	private SubscriptionService subscriptionManager;

	private Map<String, String> endpoints;

	private String apiEndpoint;

	protected List<SubscriptionResult> localSubscriptions;

	/**
	 * 
	 */
	public PlatformClient() {
		this.endpoints = new HashMap<String, String>();
		this.localSubscriptions = new ArrayList<SubscriptionResult>();
	}

	/**
	 * Connect to http://host:port/play/api/v1/
	 */
	@Override
	public void connect(String apiEndpoint, String authtoken)
			throws ClientException {
		if (apiEndpoint == null) {
			throw new ClientException("Null endpoint is not allowed");
		}
		this.apiEndpoint = apiEndpoint;

		// TODO : Metadata
		// TODO : Use basic Auth or oauth
		List<Property> props;
		try {
			props = CXFHelper.getClientFromFinalURL(
					getEndpoint(Constants.APIService), API.class).connect(
					authtoken);
			if (props != null) {
				for (Property property : props) {
					endpoints.put(property.name, property.value);
				}
			}
		} catch (APIException e) {
			throw new ClientException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.ow2.play.platform.client.api.PlatformClient#getSubscriptionManager()
	 */
	@Override
	public synchronized SubscriptionService getSubscriptionManager() throws ClientException {
		checkConnected();
		if (subscriptionManager == null) {
			subscriptionManager = new SubscriptionServiceImpl();
		}
		return subscriptionManager;
	}
	
	private void checkConnected() throws ClientException {
		if (apiEndpoint == null) {
			throw new ClientException("Please connect first");
		}
	}

	class SubscriptionServiceImpl implements SubscriptionService {

		private SubscriptionService wsClient;

		/**
		 * 
		 */
		public SubscriptionServiceImpl() {
		}

		@Override
		public SubscriptionResult subscribe(Subscription subscription)
				throws ClientException {
			// TODO : Need to ad token
			SubscriptionResult result = null;
			try {
				result = getSubscriptionService().subscribe(subscription);
				if (result != null)
					localSubscriptions.add(result);
			} catch (APIException e) {
				throw new ClientException(e);
			}

			return result;
		}

		@Override
		public boolean unsubscribe(String subscriptionID)
				throws ClientException {
			// TODO : Need to add token
			// authorize to unsubscribe for others...
			boolean result = true;
			try {
				result = getSubscriptionService().unsubscribe(subscriptionID);
			} catch (APIException e) {
				throw new ClientException(e);
			}
			if (result) {
				// TODO : Remove from cache
				// guava
			}
			return result;
		}

		private synchronized SubscriptionService getSubscriptionService() {
			if (this.wsClient == null) {
				this.wsClient = CXFHelper.getClientFromFinalURL(
						getEndpoint(Constants.SubscriptionService),
						SubscriptionService.class);
			}
			return this.wsClient;
		}

	}

	protected String getEndpoint(String serviceId) {
		return apiEndpoint.endsWith("/") ? apiEndpoint + serviceId
				: apiEndpoint + "/" + serviceId;
	}
}
