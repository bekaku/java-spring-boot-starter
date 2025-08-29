## Retrieve data

```
METHOD : GET
URL : /api/permission?page={currentPage}&size={size}&sort={#sortField,#sortType}&_q={searchParamiter}
EXAMPLE : /api/permission?page=0&size=2&sort=code,asc&_q=id>=10,frontEnd=false
```

**Request Parameter**

| Key                  | Data type                            | Description   |
| -------------------- |----------------------------------| --------------|
| page         | Int  ||
| size      | Int ||
| sort     | string,string    |example `createdDate,asc`|
| _q   | String        ||

**Response success example** :tada:

```json
{
  "dataList": [
    {
      "id": 2,
      "code": "api_client_add",
      "description": "api_client_list_add",
      "module": "AD"
    },
    {
      "id": 5,
      "code": "api_client_delete",
      "description": "api_client_delete",
      "module": "AD"
    }
  ],
  "totalPages": 10,
  "totalElements": 2,
  "last": false
}
```

## Create data

```
METHOD : POST
URL : /api/permission
```

**Request Parameter**

```
Json root name : permission
```

| Key                  | Data type | Description   |
| -------------------- |-----------| --------------|
| code         | String    ||
| remark      | String    ||
| frontEnd     | Boolean   ||
| operationType     | int       |1=crud, 2=report, 3=other|

**Request example**

```json
{
  "permission": {
    "code": "read_report",
    "remark": "appUser can read report",
    "frontEnd": false,
    "operationType": 1
  }
}
```

## Update data

```
METHOD : PUT
URL : /api/permission
```

**Request Parameter**

```
Json root name : permission
```

| Key           | Data type | Description   |
|---------------|-----------| --------------|
| id            | Long      ||
| code          | String    ||
| remark        | String    ||
| frontEnd      | Boolean   ||
| operationType | int       |1=crud, 2=report, 3=other|

**Request example**

```json
{
  "permission": {
    "id": 1,
    "code": "read_report",
    "remark": "appUser can read report",
    "frontEnd": false,
    "operationType": 1
  }
}
```
## Find one
```
METHOD : GET
URL : /api/permission/{id}
EXAMPLE : /api/permission/1
```

**Response success example** :tada:

```json
{
  "id": 1,
  "code": "api_client_list",
  "remark": null,
  "description": "Api client(List)",
  "operationType": 1,
  "frontEnd": false
}
```

## Delete data

```
METHOD : DELETE
URL : /api/permission/{id}
EXAMPLE : /api/permission/1
```
**Response success example** :tada:
```json
{
  "message": "Deleted Successful",
  "status": "OK",
  "timestamp": "2022-08-03T14:46:58.0779719"
}
```
## Get appUser access control list data

```
METHOD : GET
URL : /api/permission/userAcl
EXAMPLE : /api/permission/userAcl
```

**Response success example** :tada:

```json
{
  "permissions": [
    "api_client_view",
    "api_client_manage",
    "permission_list",
    "permission_view",
    "permission_manage",
    "role_list",
    "role_view",
    "role_manage",
    "user_list",
    "user_view",
    "user_manage"
  ],
  "menus": [
    {
      "pages": [
        {
          "noActiveLink": true,
          "iconText": "",
          "color": "info",
          "icon": "bi-house-door",
          "to": "/backend",
          "title": "nav.dashboard"
        }
      ],
      "header": ""
    },
    {
      "pages": [
        {
          "iconText": "",
          "color": "red",
          "icon": "bi-gear",
          "title": "nav.systemData",
          "items": [
            {
              "icon": "bi-shield-check",
              "permission": "permission_list",
              "to": "/backend/permission",
              "title": "model_permission"
            }
          ]
        }
      ],
      "header": "nav.developers"
    }
  ]
}
```

---