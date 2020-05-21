### 资源路径

RESTful API 的设计以资源为核心，每一个 URI 代表一种资源。因此，URI 不能包含动词，只能是名词。注意的是，形容词也是可以使用的，但是尽量少用。一般来说，不论资源是单个还是多个，API 的名词要以复数进行命名。此外，命名名词的时候，要使用小写、数字及下划线来区分多个单词。这样的设计是为了与 json 对象及属性的命名方案保持一致。例如，一个查询系统标签的接口可以进行如下设计。

```
【GET】  /v1/tags/{tag_id} 

```


同时，资源的路径应该从根到子依次如下

```
/{resources}/{resource_id}/{sub_resources}/{sub_resource_id}/{sub_resource_property}

```


我们来看一个“添加用户的角色”的设计，其中“用户”是主资源，“角色”是子资源。

```
【POST】  /v1/users/{user_id}/roles/{role_id} // 添加用户的角色

```


有的时候，当一个资源变化难以使用标准的 RESTful API 来命名，可以考虑使用一些特殊的 actions 命名。

```
/{resources}/{resource_id}/actions/{action}

```


举个例子，“密码修改”这个接口的命名很难完全使用名词来构建路径，此时可以引入 action 命名。

```
【PUT】  /v1/users/{user_id}/password/actions/modify // 密码修改

```

1  

### [#](#请求方式) 请求方式

可以通过 GET、 POST、 PUT、 PATCH、 DELETE 等方式对服务端的资源进行操作。其中：

-   GET：用于查询资源
-   POST：用于创建资源
-   PUT：用于更新服务端的资源的全部信息
-   PATCH：用于更新服务端的资源的部分信息
-   DELETE：用于删除服务端的资源。

这里，使用“用户”的案例进行回顾通过 GET、 POST、 PUT、 PATCH、 DELETE 等方式对服务端的资源进行操作。

```
【GET】          /users                # 查询用户信息列表
【GET】          /users/1001           # 查看某个用户信息
【POST】         /users                # 新建用户信息
【PUT】          /users/1001           # 更新用户信息(全部字段)
【PATCH】        /users/1001           # 更新用户信息(部分字段)
【DELETE】       /users/1001           # 删除用户信息

```

### [#](#查询参数) 查询参数

RESTful API 接口应该提供参数，过滤返回结果。其中，offset 指定返回记录的开始位置。一般情况下，它会结合 limit 来做分页的查询，这里 limit 指定返回记录的数量。

```
【GET】  /{version}/{resources}/{resource_id}?offset=0&limit=20

```


同时，orderby 可以用来排序，但仅支持单个字符的排序，如果存在多个字段排序，需要业务中扩展其他参数进行支持。

```
【GET】  /{version}/{resources}/{resource_id}?orderby={field} [asc|desc]

```


为了更好地选择是否支持查询总数，我们可以使用 count 字段，count 表示返回数据是否包含总条数，它的默认值为 false。

```
【GET】  /{version}/{resources}/{resource_id}?count=[true|false]

```

上面介绍的 offset、 limit、 orderby 是一些公共参数。此外，业务场景中还存在许多个性化的参数。我们来看一个例子。

```
【GET】  /v1/categorys/{category_id}/apps/{app_id}?enable=[1|0]&os_type={field}&device_ids={field,field,…}

```

注意的是，不要过度设计，只返回用户需要的查询参数。此外，需要考虑是否对查询参数创建数据库索引以提高查询性能