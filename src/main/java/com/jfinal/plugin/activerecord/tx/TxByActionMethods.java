/**
 * Copyright (c) 2011-2014, James Zhan 詹波 (jfinal@126.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jfinal.plugin.activerecord.tx;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import com.jfinal.aop.Interceptor;
import com.jfinal.core.ActionInvocation;
import com.jfinal.plugin.activerecord.Config;
import com.jfinal.plugin.activerecord.DbKit;
import com.jfinal.plugin.activerecord.DbPro;
import com.jfinal.plugin.activerecord.IAtom;

/**
 * TxByActionMethods
 */
public class TxByActionMethods implements Interceptor {
	
	private Set<String> actionMethodSet = new HashSet<String>();
	
	public TxByActionMethods(String... actionMethods) {
		if (actionMethods == null || actionMethods.length == 0)
			throw new IllegalArgumentException("actionMethods can not be blank.");
		
		for (String actionMethod : actionMethods)
			actionMethodSet.add(actionMethod.trim());
	}
	
	public void intercept(final ActionInvocation ai) {
		Config config = Tx.getConfigWithTxConfig(ai);
		if (config == null)
			config = DbKit.getConfig();
		
		if (actionMethodSet.contains(ai.getMethodName())) {
			DbPro.use(config.getName()).tx(new IAtom(){
				public boolean run() throws SQLException {
					ai.invoke();
					return true;
				}});
		}
		else {
			ai.invoke();
		}
	}
}







