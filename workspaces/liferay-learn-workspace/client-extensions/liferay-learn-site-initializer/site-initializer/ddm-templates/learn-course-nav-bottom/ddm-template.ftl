<#assign
	navigationJSONObject = jsonFactoryUtil.createJSONObject(navigation.getData())
	childrenJSONArray = navigationJSONObject.getJSONArray("children")
	parentJSONObject = navigationJSONObject.getJSONObject("parent")
	siblingsJSONArray = navigationJSONObject.getJSONArray("siblings")
	nextLesson =
		{
			"title": "",
			"url": ""
		}
/>

<#if childrenJSONArray.length() gt 0>
	<#assign nextLesson = childrenJSONArray.getJSONObject(0) />
</#if>

<#if siblingsJSONArray.length() gt 0>
	<#list 0..siblingsJSONArray.length()-1 as i>
		<#if .vars["reserved-article-title"].data == siblingsJSONArray.getJSONObject(i).title>
			<#assign previousLesson = siblingsJSONArray.getJSONObject(i-1) />
			<#if !nextLesson.title?has_content>
				<#assign nextLesson = siblingsJSONArray.getJSONObject(i+1) />
			</#if>
		</#if>
	</#list>
</#if>

<a href=${nextLesson.url}>
	<div class="course-nav-bottom__banner d-flex">
		<div class="banner-options d-flex">
			<div class="banner-next-container">
				Up next
			</div>

			<div class="banner-title">
				${nextLesson.title}
			</div>
		</div>

		<div class="banner-icon">
			<svg
				class="lexicon-icon lexicon-icon-order-arrow-right"
				role="presentation"
				viewBox="0 0 512 512"
				>
					<use xlink:href="/o/admin-theme/images/clay/icons.svg#order-arrow-right"></use>
			</svg>
		</div>
	</div>
</a>

<div class="course-nav-bottom__menu d-flex">
	<div class="menu-previous-lesson d-flex">
			<a href=${previousLesson.url}
			<div class="previous-lesson-icon">
				<svg
				class="lexicon-icon lexicon-icon-order-arrow-left"
				role="presentation"
				viewBox="0 0 512 512"
				>
					<use xlink:href="/o/admin-theme/images/clay/icons.svg#order-arrow-left"></use>
				</svg>
			</div>
		</a>

		<div class="previous-lesson-title">
			Previous Lesson
		</div>
	</div>

	<div class="menu-sign-in">
		<a href="${htmlUtil.escape(themeDisplay.getURLSignIn())}">Sign in</a> to save your progress!
	</div>
</div>

<style>
	.course-nav-bottom__banner {
		align-items: center;
		background-color: #0B5FFF;
		border-radius: 10px;
		color: #FFFFFF;
		justify-content: space-between;
		padding: 12px;
		.banner-options {
			align-items: center;
			gap: 1rem;

			.banner-next-container {
				background-color: #28293426;
				border-radius: 10px;
				font-size: 14px;
				font-weight: 600;
				padding: 12px;
				text-align: center;
			}
			.banner-title {
			font-size: 20px;
				font-weight: 600;
			}
		}

		.banner-icon {
			padding: 0 24px;
			svg {
				height: 24px;
				width: 24px;
			}
		}
	}
	.course-nav-bottom__menu {
		align-items: center;
		border-top: 1px solid #E2E2E4;
		color: #2B3A4B;
		font-weight: 600;
		height: 40px;
		justify-content: space-between;
		margin-top: 1rem;
		.menu-previous-lesson {
			gap: 0.5rem;
			padding: 8px 16px;
		}
		.menu-sign-in a {
			text-decoration: underline;
		}
		.previous-lesson-icon {
			color: #2B3A4B;
		}
	}
</style>