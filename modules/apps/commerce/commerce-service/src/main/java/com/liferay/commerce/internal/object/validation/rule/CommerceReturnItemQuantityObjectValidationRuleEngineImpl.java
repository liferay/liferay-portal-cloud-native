/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.object.validation.rule;

import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.service.CommerceOrderItemLocalService;
import com.liferay.object.scope.ObjectDefinitionScoped;
import com.liferay.object.validation.rule.ObjectValidationRuleEngine;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.BigDecimalUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;

import java.math.BigDecimal;

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
public class CommerceReturnItemQuantityObjectValidationRuleEngineImpl
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

				CommerceOrderItem commerceOrderItem =
					_commerceOrderItemLocalService.fetchCommerceOrderItem(
						GetterUtil.getLong(
							properties.get(
								"r_commerceOrderItemToCommerceReturnItems_" +
									"commerceOrderItemId")));

				if (commerceOrderItem == null) {
					return false;
				}

				BigDecimal quantity = BigDecimal.valueOf(
					GetterUtil.getLong(properties.get("quantity")));

				if (BigDecimalUtil.gte(quantity, BigDecimal.ZERO) &&
					BigDecimalUtil.lte(
						quantity, commerceOrderItem.getQuantity())) {

					return true;
				}

				return false;
			}
		).build();
	}

	@Override
	public List<String> getAllowedObjectDefinitionNames() {
		return Arrays.asList("CommerceReturnItem");
	}

	@Override
	public String getKey() {
		return "javaDelegate#CommerceReturnItem#quantity";
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "commerce-return-item-quantity");
	}

	@Reference
	private CommerceOrderItemLocalService _commerceOrderItemLocalService;

	@Reference
	private Language _language;

}