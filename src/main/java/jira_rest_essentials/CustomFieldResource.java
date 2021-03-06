package jira_rest_essentials;

import com.atlassian.jira.web.action.admin.translation.TranslationManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import java.util.Set;
import java.util.stream.Collectors;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.fields.CustomField;
import org.springframework.util.StringUtils;

/**
 * @author Jens Piegsa
 */
@Path("customfield")
public class CustomFieldResource {

	private static final Logger log = LogManager.getLogger("atlassian.plugin");

	private static CustomFieldManager customFieldManager() {
		return ComponentAccessor.getCustomFieldManager();
	}

	private static TranslationManager translationManager() {
		return ComponentAccessor.getTranslationManager();
	}

	@GET
	@Produces({MediaType.APPLICATION_JSON})
	@Path("description")
	public Response getScreen(@QueryParam("id") final Long id) {

		if (id != null) {
			log.debug("get custom field description id: " + id);
			return Response.ok(getCustomFieldById(id)).build();
		}
		log.debug("get all custom field descriptions");
		return Response.ok(getCustomFields()).build();
	}

	@GET
	@Produces({MediaType.APPLICATION_JSON})
	@Path("translations")
	public Response getTranslations() {
		final Set<String> installedLocaleNames = getInstalledLocaleNames();
		final List<CustomField> customFields = customFieldManager().getCustomFieldObjects();

		List<CustomFieldTranslation> translations = new ArrayList<>();
		for (CustomField customField : customFields) {
			Map<String, String> namesByLocale = new LinkedHashMap<>();
			Map<String, String> descriptionsByLocale = new LinkedHashMap<>();
			namesByLocale.put("default", customField.getUntranslatedName());
			descriptionsByLocale.put("default", customField.getUntranslatedDescription());
			for (String localeName : installedLocaleNames) {
				final Locale locale = StringUtils.parseLocaleString(localeName);
				final String nameTranslation = translationManager().getCustomFieldNameTranslation(customField, locale);
				final String descriptionTranslation = translationManager().getCustomFieldDescriptionTranslation(customField, locale);
				if (nameTranslation != null && !nameTranslation.isEmpty()) {
					namesByLocale.put(localeName, nameTranslation);
				}
				if (descriptionTranslation != null && !descriptionTranslation.isEmpty()) {
					descriptionsByLocale.put(localeName, descriptionTranslation);
				}
			}
			final CustomFieldTranslation customFieldTranslation = new CustomFieldTranslation();
			customFieldTranslation.setId(customField.getId());
			customFieldTranslation.setName(namesByLocale);
			customFieldTranslation.setDescription(descriptionsByLocale);
			translations.add(customFieldTranslation);
		}
		return Response.ok(translations).build();
	}

	private Set<String> getInstalledLocaleNames() {
		final Set<String> installedLocaleNames = translationManager().getInstalledLocales().keySet();
		debugLocales(installedLocaleNames);
		return installedLocaleNames;
	}

	private void debugLocales(final Set<String> localeNames) {
		log.info(localeNames.stream()
				.map(n -> "'" + n + "' -> " + StringUtils.parseLocaleString(n))
				.collect(Collectors.joining("\n\t", "All locales:\n\t", "\n")));
	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("translations")
	public Response putTranslations(final List<CustomFieldTranslation> translations) {
		for (CustomFieldTranslation translation : translations) {
			final CustomField customField = customFieldManager().getCustomFieldObject(translation.getId());
			if (customField != null) {
				final Map<String, String> nameByLocale = translation.getName();
				final Map<String, String> descriptionByLocale = translation.getDescription();
				final Set<String> localeNames = nameByLocale.keySet();
				for (String localeName : localeNames) {
					if (!"default".equals(localeName)) {
						final Locale locale = StringUtils.parseLocaleString(localeName);
						if (locale != null) {
							final String name = nameByLocale.get(localeName);
							final String description = descriptionByLocale.get(localeName);

							if (name != null) {
								log.info("Setting translation " + locale + " of "
										+ customField.getFieldName() + "[" + customField.getId() + "] - "
										+ "name: '" + name + "' description:  '" + description + "' ...");
								translationManager().setCustomFieldTranslation(customField, locale, name, description);
							} else {
								translationManager().deleteCustomFieldTranslation(customField, locale);
							}
						} else {
							log.warn("Could not parse locale " + localeName + ".");
						}
					}
				}
			} else {
				log.warn("Could not find custom field " + translation.getId() + " -> ignoring translation.");
			}
		}
		return Response.status(Response.Status.ACCEPTED).build();
	}

	private Map<String, String> getCustomFields() {
		final Collection<CustomField> customFields = customFieldManager().getCustomFieldObjects();
		final Map<String, String> screens = new HashMap<>();
		for (final CustomField customField : customFields) {
			screens.put(customField.getId(), customField.getDescription());
		}

		return screens;
	}

	private Map<String, String> getCustomFieldById(final long id) {
		final CustomField customField = customFieldManager().getCustomFieldObject(id);
		return Collections.singletonMap(customField.getId(), customField.getName());
	}
}
