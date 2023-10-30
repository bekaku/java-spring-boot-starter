## Retrieve data

```
METHOD : GET
URL : /api/role?page={currentPage}&size={size}&sort={#sortField,#sortType}&_q={searchParamiter}
EXAMPLE : /api/role?page=0&size=2&sort=code,asc&_q=id>=10,frontEnd=false
```

**Request Parameter**

| Key  | Data type     | Description   |
|------|---------------| --------------|
| page | Int           ||
| size | Int           ||
| sort | string,string |example `createdDate,asc`|
| _q   | String        ||

**Response success example** :tada:

```json
{
  "dataList": [
    {
      "id": 1,
      "name": "Developer",
      "nameEn": "Developer",
      "active": true,
      "frontEnd": false,
      "selectdPermissions": [],
      "companySelected": null
    },
    {
      "id": 2,
      "name": "general user",
      "nameEn": "general user",
      "active": true,
      "frontEnd": true,
      "selectdPermissions": [],
      "companySelected": null
    }
  ],
  "totalPages": 1,
  "totalElements": 2,
  "last": true
}
```

## Create data

```
METHOD : POST
URL : /api/role
```

**Request Parameter**

```
Json root name : role
```

| Key                  | Data type | Description   |
| -------------------- |-----------| --------------|
| code         | String    ||
| nameEn      | String    ||
| active     | Boolean   ||
| frontEnd     | Boolean   ||
| companySelected     | Long      ||
| selectdPermissions     | Long[]    | |

**Request example**

```json
{
  "role": {
    "name": "It Support",
    "nameEn": "Support",
    "active": true,
    "frontEnd": false,
    "companySelected": 0,
    "selectdPermissions": [
      2,
      3,
      4
    ]
  }
}
```

## Update data

```
METHOD : PUT
URL : /api/role
```

**Request Parameter**

```
Json root name : role
```

| Key           | Data type | Description   |
|---------------|-----------| --------------|
| id            | Long      ||
| code         | String    ||
| nameEn      | String    ||
| active     | Boolean   ||
| frontEnd     | Boolean   ||
| companySelected     | Long      ||
| selectdPermissions     | Long[]    | |

**Request example**

```json
{
  "role": {
    "id": 2,
    "name": "It Support",
    "nameEn": "Support",
    "active": true,
    "frontEnd": false,
    "companySelected": 0,
    "selectdPermissions": [
      2,
      3,
      4
    ]
  }
}
```

## Find one

```
METHOD : GET
URL : /api/role/{id}
EXAMPLE : /api/role/1
```

**Response success example** :tada:

```json
{
  "id": 1,
  "name": "Developer",
  "nameEn": "Developer",
  "active": true,
  "frontEnd": false,
  "selectdPermissions": [
    1,
    3,
    2
  ],
  "companySelected": null
}
```

## Delete data

```
METHOD : DELETE
URL : /api/role/{id}
EXAMPLE : /api/role/1
```

**Response success example** :tada:

```json
{
  "message": "Deleted Successful",
  "status": "OK",
  "timestamp": "2022-08-03T14:46:58.0779719"
}
```
## Find all role for backend

```
METHOD : GET
URL : /api/role/findAllBackend
EXAMPLE : 
```

**Response success example** :tada:

```json
[
  {
    "id": 1,
    "name": "Developer",
    "nameEn": "Developer",
    "active": true,
    "frontEnd": false,
    "selectdPermissions": [],
    "companySelected": null
  }
]
```

## Find all role for frontend and company is null

```
METHOD : GET
URL : /api/role/findAllSystemFrontend
EXAMPLE : 
```

**Response success example** :tada:

```json
[
  {
    "id": 2,
    "name": "general user",
    "nameEn": "general user",
    "active": true,
    "frontEnd": true,
    "selectdPermissions": [],
    "companySelected": null
  }
]
```

## Find all company's role depend on user JWT authen key

```
METHOD : GET
URL : /api/role/findAllByCompany?page={currentPage}&size={size}&sort={#sortField,#sortType}&_q={searchParamiter}
EXAMPLE : /api/role/findAllByCompany?page=0&size=2&sort=code,asc&_q=id>=10,code=false
```
**Request Parameter**

| Key  | Data type     | Description   |
|------|---------------| --------------|
| page | Int           ||
| size | Int           ||
| sort | string,string |example `createdDate,asc`|
| _q   | String        ||
**Response success example** :tada:

```json
[
  {
    "id": 2,
    "name": "general user",
    "nameEn": "general user",
    "active": true,
    "frontEnd": true,
    "selectdPermissions": [],
    "companySelected": null
  }
]
```

## Create company's role data by company's admin

```
METHOD : POST
URL : /api/role/createByCompany
```
## Update company's role data by company's admin

```
METHOD : PUT
URL : /api/role/updateByCompany
```

## Delete company's role data by company's admin

```
METHOD : DELETE
URL : /api/role/deleteByCompany/{id}
EXAMPLE : /api/role/deleteByCompany/1
```

---