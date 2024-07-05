/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.object.validation.rule;

import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.object.scope.ObjectDefinitionScoped;
import com.liferay.object.validation.rule.ObjectValidationRuleEngine;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Crescenzo Rega
 */
@Component(service = ObjectValidationRuleEngine.class)
public class CommerceReturnCommerceOrderStatusObjectValidationRuleEngineImpl
	implements ObjectDefinitionScoped, ObjectValidationRuleEngine {

	@Override
	public Map<String, Object> execute(
		Map<String, Object> inputObjects, String script) {

		return HashMapBuilder.<String, Object>put(
			"validationCriteriaMet",
			() -> {
				Map<String, Object> entryDTO =
					(Map<String, Object>)inputObjects.get("entryDTO");

				Map<String, Object> properties =
					(Map<String, Object>)entryDTO.get("properties");

				CommerceOrder commerceOrder =
					_commerceOrderLocalService.fetchCommerceOrder(
						GetterUtil.getLong(
							properties.get(
								"r_commerceOrderToCommerceReturns_" +
									"commerceOrderId")));

				if (commerceOrder == null) {
					return false;
				}

				if (commerceOrder.getOrderStatus() ==
						CommerceOrderConstants.ORDER_STATUS_COMPLETED) {

					return true;
				}

				return false;
			}
		).build();
	}

	@Override
	public List<String> getAllowedObjectDefinitionNames() {
		return Arrays.asList("CommerceReturn");
	}

	@Override
	public String getKey() {
		return "javaDelegate#CommerceReturn#commerceOrderStatus";
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "commerce-return-commerce-order-status");
	}

	@Reference
	private CommerceOrderLocalService _commerceOrderLocalService;

	@Reference
	private Language _language;

}