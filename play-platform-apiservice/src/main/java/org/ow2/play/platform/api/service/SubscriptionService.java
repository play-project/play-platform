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
package org.ow2.play.platform.api.service;

import java.util.List;
import java.util.logging.Level;

import javax.jws.WebService;

import org.ow2.play.governance.api.GovernanceExeption;
import org.ow2.play.governance.api.SubscriptionManagement;
import org.ow2.play.governance.api.bean.Topic;
import org.ow2.play.platform.api.APIException;
import org.ow2.play.platform.api.bean.Subscription;
import org.ow2.play.platform.api.bean.SubscriptionResult;
import org.ow2.play.service.registry.api.Constants;
import org.ow2.play.service.registry.api.Registry;
import org.ow2.play.service.registry.api.RegistryException;
import org.petalslink.dsb.cxf.CXFHelper;

import com.google.common.collect.Lists;
import com.sun.istack.logging.Logger;

/**
 * @author chamerling
 * 
 */
@WebService
public class SubscriptionService implements
		org.ow2.play.platform.api.SubscriptionService {

	private Registry registry;

	private static Logger logger = Logger.getLogger(SubscriptionService.class);

	@Override
	public SubscriptionResult subscribe(Subscription subscription)
			throws APIException {
		logger.info("Got a subscribe " + subscription);

		if (subscription == null) {
			throw new APIException("Can not subscribe null");
		}

		if (subscription.subscriber == null) {
			throw new APIException("Can not subscribe for null endpoint");
		}

		if (subscription.topic == null) {
			throw new APIException("Can not subscribe on null topic");
		}

		SubscriptionResult result = new SubscriptionResult();
		result.initialSubscription = subscription;

		// A. Store activity, monitor

		// B. Send to gov : Subscribe and then push to subscriptions store
		SubscriptionManagement client = getSubscriptionManagement();
		// get the subscription endpoint to be put in the subscription
		String subcribeTo = getSubscribeToEndpoint();

		org.ow2.play.governance.api.bean.Subscription s = new org.ow2.play.governance.api.bean.Subscription();
		s.setProvider(subcribeTo);
		s.setSubscriber(subscription.subscriber);
		Topic t = new Topic();
		t.setName(subscription.topic.name);
		t.setNs(subscription.topic.ns);
		t.setPrefix(subscription.topic.prefix);
		s.setTopic(t);

		try {
			List<org.ow2.play.governance.api.bean.Subscription> list = client
					.subscribe(Lists.newArrayList(s));
			if (list == null || list.size() == 0) {
				throw new APIException("Can not get any subscription result");
			}

			org.ow2.play.governance.api.bean.Subscription subcriptionResult = list
					.get(0);
			if (subcriptionResult != null) {
				result.subscriptionID = subcriptionResult.getId();
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Governance returned " + subcriptionResult);
				}
			} else {
				// ...
				logger.warning("Can not find subscription result");
				// throw?
			}

		} catch (GovernanceExeption e) {
			e.printStackTrace();
			throw new APIException("Can not subscribe");
		}

		// C. ...
		return result;
	}

	@Override
	public boolean unsubscribe(String subscriptionID) throws APIException {
		boolean result = true;
		logger.info("Got an unsubscribe " + subscriptionID);

		// A. Store activity

		// B. Send to gov : Subscribe and then push to subscriptions store
		SubscriptionManagement client = getSubscriptionManagement();
		org.ow2.play.governance.api.bean.Subscription s = new org.ow2.play.governance.api.bean.Subscription();
		s.setId(subscriptionID);

		try {
			List<org.ow2.play.governance.api.bean.Subscription> unsub = client
					.unsubscribe(Lists.newArrayList(s));
			result = unsub != null && unsub.size() > 0;

			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Governance returned : " + unsub);
			}

		} catch (GovernanceExeption e) {
			e.printStackTrace();
			throw new APIException("Can not unsubscribe for " + subscriptionID);
		}

		return result;
	}

	public SubscriptionManagement getSubscriptionManagement()
			throws APIException {
		try {
			return CXFHelper.getClientFromFinalURL(registry
					.get(Constants.GOVERNANCE_SUBSCRIPTIONMANAGEMENT_SERVICE),
					SubscriptionManagement.class);
		} catch (RegistryException e) {
			final String msg = "Can not retrieve service information";
			if (logger.isLoggable(Level.WARNING)) {
				logger.log(Level.WARNING, msg);
			}
			throw new APIException(msg);
		}
	}

	public String getSubscribeToEndpoint() throws APIException {
		try {
			return registry.get(Constants.DSB_PRODUCER);
		} catch (RegistryException e) {
			final String msg = "Can not retrieve producer service information";
			if (logger.isLoggable(Level.WARNING)) {
				logger.log(Level.WARNING, msg);
			}
			throw new APIException(msg);
		}
	}

	public void setRegistry(Registry registry) {
		this.registry = registry;
	}

}
