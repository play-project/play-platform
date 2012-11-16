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

import java.util.ArrayList;
import java.util.List;

import javax.jws.WebMethod;

import org.ow2.play.platform.api.bean.Property;
import org.ow2.play.platform.client.api.ClientException;
import org.ow2.play.service.registry.api.Entry;
import org.ow2.play.service.registry.api.Registry;
import org.ow2.play.service.registry.api.RegistryException;

import com.sun.istack.logging.Logger;

/**
 * @author chamerling
 * 
 */
public class API implements org.ow2.play.platform.api.API {

	private static Logger logger = Logger.getLogger(API.class);

	private Registry registry;

	@Override
	@WebMethod
	public List<Property> connect(String authtoken) throws ClientException {
		// TODO : Check credentials

		List<Property> result = new ArrayList<Property>();
		try {
			List<Entry> entries = registry.entries();
			for (Entry entry : entries) {
				result.add(new Property(entry.key, entry.value));
			}
		} catch (RegistryException e) {
			throw new ClientException("Error while connecting to API");
		}

		// TODO : more...

		return result;
	}

	public void setRegistry(Registry registry) {
		this.registry = registry;
	}

}
