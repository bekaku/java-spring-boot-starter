## Retrieve data

```
METHOD : GET
URL : /api/appRole?page={currentPage}&size={size}&sort={#sortField,#sortType}&_q={searchParamiter}
EXAMPLE : /api/appRole?page=0&size=2&sort=code,asc&_q=id>=10,frontEnd=false
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
      "name": "general appUser",
      "nameEn": "general appUser",
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
URL : /api/appRole
```

**Request Parameter**

```
Json root name : appRole
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
  "appRole": {
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
URL : /api/appRole
```

**Request Parameter**

```
Json root name : appRole
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
  "appRole": {
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
URL : /api/appRole/{id}
EXAMPLE : /api/appRole/1
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
URL : /api/appRole/{id}
EXAMPLE : /api/appRole/1
```

**Response success example** :tada:

```json
{
  "message": "Deleted Successful",
  "status": "OK",
  "timestamp": "2022-08-03T14:46:58.0779719"
}
```
## Find all appRole for backend

```
METHOD : GET
URL : /api/appRole/findAllBackend
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

## Find all appRole for frontend and company is null

```
METHOD : GET
URL : /api/appRole/findAllSystemFrontend
EXAMPLE : 
```

**Response success example** :tada:

```json
[
  {
    "id": 2,
    "name": "general appUser",
    "nameEn": "general appUser",
    "active": true,
    "frontEnd": true,
    "selectdPermissions": [],
    "companySelected": null
  }
]
```

## Find all company's appRole depend on appUser JWT authen key

```
METHOD : GET
URL : /api/appRole/findAllByCompany?page={currentPage}&size={size}&sort={#sortField,#sortType}&_q={searchParamiter}
EXAMPLE : /api/appRole/findAllByCompany?page=0&size=2&sort=code,asc&_q=id>=10,code=false
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
    "name": "general appUser",
    "nameEn": "general appUser",
    "active": true,
    "frontEnd": true,
    "selectdPermissions": [],
    "companySelected": null
  }
]
```

## Create company's appRole data by company's admin

```
METHOD : POST
URL : /api/appRole/createByCompany
```
## Update company's appRole data by company's admin

```
METHOD : PUT
URL : /api/appRole/updateByCompany
```

## Delete company's appRole data by company's admin

```
METHOD : DELETE
URL : /api/appRole/deleteByCompany/{id}
EXAMPLE : /api/appRole/deleteByCompany/1
```

---