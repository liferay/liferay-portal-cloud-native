/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.demo.internal;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.instance.lifecycle.BasePortalInstanceLifecycleListener;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.OrganizationConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.module.framework.ModuleServiceLifecycle;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.Portal;

import java.util.Calendar;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pei-Jung Lan
 */
@Component(service = PortalInstanceLifecycleListener.class)
public class UsersDemo extends BasePortalInstanceLifecycleListener {

	@Override
	public void portalInstanceRegistered(Company company) throws Exception {
		if (company.getCompanyId() != _portal.getDefaultCompanyId()) {
			return;
		}

		_addTestCompanyAdminUser(company);

		Organization organization = _addTestOrganization(
			company.getCompanyId());

		_addTestOrganizationOwnerUser(company, organization);

		_addTestUnprivilegedUser(company);
	}

	private User _addTestCompanyAdminUser(Company company) throws Exception {
		String screenName = "test-company-admin";

		User user = _userLocalService.fetchUserByScreenName(
			company.getCompanyId(), screenName);

		if (user != null) {
			return user;
		}

		Group group = _groupLocalService.getGroup(
			company.getCompanyId(), GroupConstants.GUEST);

		user = _addUser(company, screenName, new long[] {group.getGroupId()});

		Role role = _roleLocalService.getRole(
			company.getCompanyId(), RoleConstants.ADMINISTRATOR);

		_userLocalService.addRoleUser(role.getRoleId(), user);

		return user;
	}

	private Organization _addTestOrganization(long companyId) throws Exception {
		String name = "test-organization";

		Organization organization = _organizationLocalService.fetchOrganization(
			companyId, name);

		if (organization != null) {
			return organization;
		}

		User user = _getAdminUser(companyId);

		return _organizationLocalService.addOrganization(
			user.getUserId(),
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID, name, false);
	}

	private User _addTestOrganizationOwnerUser(
			Company company, Organization organization)
		throws Exception {

		String screenName = "test-organization-owner";

		User user = _userLocalService.fetchUserByScreenName(
			company.getCompanyId(), screenName);

		if (user != null) {
			return user;
		}

		user = _addUser(
			company, screenName, new long[] {organization.getGroupId()});

		_userLocalService.addOrganizationUser(
			organization.getOrganizationId(), user.getUserId());

		Role role = _roleLocalService.getRole(
			company.getCompanyId(), RoleConstants.ORGANIZATION_OWNER);

		_userGroupRoleLocalService.addUserGroupRoles(
			new long[] {user.getUserId()}, organization.getGroupId(),
			role.getRoleId());

		return user;
	}

	private User _addTestUnprivilegedUser(Company company) throws Exception {
		String screenName = "test-unprivileged";

		User user = _userLocalService.fetchUserByScreenName(
			company.getCompanyId(), screenName);

		if (user != null) {
			return user;
		}

		return _addUser(company, screenName, null);
	}

	private User _addUser(Company company, String screenName, long[] groupIds)
		throws Exception {

		User adminUser = _getAdminUser(company.getCompanyId());
		String firstName = screenName;
		String lastName = screenName;

		User user = _userLocalService.addUser(
			adminUser.getUserId(), company.getCompanyId(), false, "test",
			"test", false, screenName, screenName + "@liferay.com",
			company.getLocale(), firstName, StringPool.BLANK, lastName, 0, 0,
			true, Calendar.JANUARY, 1970, 1970, StringPool.BLANK,
			UserConstants.TYPE_REGULAR, groupIds, null, null, null, false,
			null);

		user.setEmailAddressVerified(true);

		return _userLocalService.updateUser(user);
	}

	private User _getAdminUser(long companyId) throws Exception {
		Role role = _roleLocalService.getRole(
			companyId, RoleConstants.ADMINISTRATOR);

		List<User> users = _userLocalService.getRoleUsers(
			role.getRoleId(), 0, 1);

		return users.get(0);
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference(target = ModuleServiceLifecycle.PORTLETS_INITIALIZED)
	private ModuleServiceLifecycle _moduleServiceLifecycle;

	@Reference
	private OrganizationLocalService _organizationLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private UserGroupRoleLocalService _userGroupRoleLocalService;

	@Reference
	private UserLocalService _userLocalService;

}