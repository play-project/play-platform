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
package org.ow2.play.platform.client.api;

import org.ow2.play.platform.api.APIException;

/**
 * @author chamerling
 * 
 */
public class ClientException extends APIException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9008707026189587876L;

	public ClientException() {
		super();
	}

	public ClientException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public ClientException(String arg0) {
		super(arg0);
	}

	public ClientException(Throwable arg0) {
		super(arg0);
	}

}
