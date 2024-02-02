/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.model.listener;

import com.liferay.account.model.AccountEntryOrganizationRel;
import com.liferay.object.internal.search.ObjectEntryBatchReindexer;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.transaction.TransactionCommitCallbackUtil;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Feliphe Marinho
 * @author Gabriel Albuquerque
 */
@Component(service = ModelListener.class)
public class AccountEntryOrganizationRelModelListener
	extends BaseModelListener<AccountEntryOrganizationRel> {

	@Override
	public void onAfterCreate(
			AccountEntryOrganizationRel accountEntryOrganizationRel)
		throws ModelListenerException {

		TransactionCommitCallbackUtil.registerCallback(
			() -> {
				_reindex(accountEntryOrganizationRel);

				return null;
			});
	}

	@Override
	public void onAfterRemove(
			AccountEntryOrganizationRel accountEntryOrganizationRel)
		throws ModelListenerException {

		TransactionCommitCallbackUtil.registerCallback(
			() -> {
				_reindex(accountEntryOrganizationRel);

				return null;
			});
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_objectEntryBatchReindexers = ServiceTrackerListFactory.open(
			bundleContext, ObjectEntryBatchReindexer.class);
	}

	@Deactivate
	protected void deactivate() {
		_objectEntryBatchReindexers.close();
	}

	private void _reindex(
		AccountEntryOrganizationRel accountEntryOrganizationRel) {

		for (ObjectEntryBatchReindexer objectEntryBatchReindexer :
				_objectEntryBatchReindexers) {

			objectEntryBatchReindexer.reindex(
				accountEntryOrganizationRel.getAccountEntryId(),
				accountEntryOrganizationRel.getCompanyId());
		}
	}

	private ServiceTrackerList<ObjectEntryBatchReindexer>
		_objectEntryBatchReindexers;

}