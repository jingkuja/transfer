/**
 * JSONSerializable.java
 * Created on Dec 1, 2013 2:09:37 PM
 * Copyright (c) 2012-2014 Aves Team of Sichuan Abacus Co.,Ltd. All rights reserved.
 */
package org.aves.transfer.json;

import java.io.Serializable;

/**
 * @author Steven Chen
 * 
 */
public interface JSONSerializable extends Serializable {

	void unmarshalJSON(JSONObject json);

	JSONObject marshalJSON();

}
