# JIRA REST Essentials

[![Build Status](https://travis-ci.org/JensPiegsa/jira-rest-essentials.svg?branch=master)](https://travis-ci.org/JensPiegsa/jira-rest-essentials)
[![codecov](https://codecov.io/gh/JensPiegsa/jira-rest-essentials/branch/master/graph/badge.svg)](https://codecov.io/gh/JensPiegsa/jira-rest-essentials)
[![Donate](https://img.shields.io/badge/Donate-PayPal-green.svg)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=3WB8AXMP4VY98)

This JIRA plugin exists to close some gaps of the [JIRA REST API](https://docs.atlassian.com/jira/REST/ondemand/). 

### FieldScreen

#### `GET https://example.com/jira/rest/essentials/1.0/screen`

* lists all screens by id and name

#### `GET https://example.com/jira/rest/essentials/1.0/screen?projectKey={projectKey}`

* lists all screens associated with a given project

#### `GET https://example.com/jira/rest/essentials/1.0/screen?name={screenName}`

* returns the id for a screen with the given name

### CustomField

#### `GET https://example.com/jira/rest/essentials/1.0/customfield/description`

* lists all custom field descriptions by field (with prefix `custom_*`)

#### `GET https://example.com/jira/rest/essentials/1.0/customfield/description?id={id}`

* returns a custom field description for a given field (id as `long` without prefix)

#### `GET https://example.com/jira/rest/essentials/1.0/customfield/translations`

* returns all available translations for all fields

#### `PUT https://example.com/jira/rest/essentials/1.0/customfield/translations`

Request body example:

```json
[
    {
        "id" : "customfield_12345",
        "name" : {
            "de_DE" : "Hinweis",
            "en_UK" : "Hint"
        },
        "description" : {
            "de_DE" : "Zusatzinformation",
            "en_UK" : "Additional information"
        }
    }
]
```


#### `GET https://example.com/jira/rest/essentials/1.0/status/translations`

* returns all available translations of status name / description

#### `PUT https://example.com/jira/rest/essentials/1.0/status/translations`

Request body example:

```json
[
    {
        "id": "1",
        "name": {
            "de": "Offen",
            "en_US": "Open"
        },
        "description": {
            "de": "Ein offener Vorgang.",
            "en_US": "An open issue."
        }
    },
    {
        "id": "2",
        "name": {
            "de": "Geschlossen",
            "en_US": "Closed"
        },
        "description": {
            "de": "Ein geschlossener Vorgang.",
            "en_US": "A closed issue."
        }
    }
]
```

#### `GET https://example.com/jira/rest/essentials/1.0/workflowscheme?projectKey=EXAMPLE`

* returns workflow scheme id for given project

### Comments

#### `POST https://example.com/jira/rest/essentials/1.0/comment?issueKey=EXAMPLE-123`

Request body example:

```json
{
  "author": "someuser",
  "body": "secret comment",
  "projectAdminsOnly": true
}
```

### Create option

#### `PUT https://example.com/jira/rest/essentials/1.0/option?customfieldId=customfield_12345&value=Yes`

* returns the id of the created option
* Limit: works only with the first configuration scheme of a custom field.

### Disabled options

#### `GET https://example.com/jira/rest/essentials/1.0/disabled-options`

* returns all options marked as disabled

---

### Build the Add-on ###

* Use Maven to build a deployable jar file: `mvn clean package`

