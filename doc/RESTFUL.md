# REST API实践指南
## 概念
* 概述：`REST`Representational State Transfer，即（资源）的具象化（表现化）的状态转化
  * 概要说明：它使用URL来标识资源、HTTP的动词表现转变。
    * URL部分一般可直接使用领域模型的领域对象或服务作为片段
    * 这种以模型中名词和HTTP动词的组合表现形式，有简洁高效且直接的表现力，无论开发者还是用户容易顷刻间明白其作用
      * `RESTFUL`示例：`GET /users`获取用户集合
      * 非`RESTFUL`示例：`GET /listAllUsers`获取用户集合
* 具体：可查看下列文档
  * [理解RESTful架构](https://www.ruanyifeng.com/blog/2011/09/restful.html)
  * [RESTful API 设计指南](http://www.ruanyifeng.com/blog/2014/05/restful_api.html)
  * [REST资源命名指南](http://restful.p2hp.com/home/resource-naming) 该文步子有点大，复杂业务下难命名可以参考
  * [IETF-请求方法](https://www.rfc-editor.org/rfc/rfc7231#section-4)
    * 特别指出如下URL携带参数的方式是符合协议的，使用`springMVC`的开发可以查看`@MatrixVariable`注解响应的作用
      * 一个参数：query params、path params多个值：`a=v1,v2 | a=v1&a=v2`
      * 多个参数：如path params `/lisi;gander=1;`
  * [“一把梭：REST API 全用 POST”](https://coolshell.cn/articles/22173.html)

## 难点
* 资源
  * 哪些事物可以为资源
    * 模型中的业务对象：如`user`
    * 模型里的服务：如：转账-`POST /transaction from=a&to=b`、运输-`POST /transportation from=a&to=b`
      * 不好区分，当`/user/{username}/avator`使用一种动词仍不能满足业务的操作粒度时可以考虑使用，。。。。。。。。。。。。。
  * 是否复数：
    * 从现实考量资源被操作时是单数还是复数，使用对应的单复数即可。
      * 由于`RESTFUL`之外的URL编写风格很少用复数名词，加上大量文章都以集合资源作为典型来讲解`RESTFUL`有的人会欠考虑的都是用资源的复数形式，`RESTFUL`是描述资源的状态转移（变更）的，它是一种基于客观合理性的设计风格，其客观存在是单数那就采用单数 
      * 举例：`GET /users`，使用者很容易明白是获取所有的用户
      * 举例：`GET /user`，当使用者明白`RESTFUL`由于资源是存在多个而使用复数的原因，则`/user`为单数很容易推断其为当前用户，这样的推断可以使
        简写强迫症患者不用写`current_user`
  * 命名要点：
    * 以**创建/操作**的**资源/服务**的名字入手，不要限于业务动作或者产品名。
      * 正例：登录是个动作，其结果在模型角度是**创建**`会话`，这里`会话`就是资源，即可命名该资源为`session`
      * 反例：产品定义了个功能为门户，用于归纳集合各类网站的单点登录入口，不能取名`gateway`，这里就可以考虑使用的资源命名为`app`|`client`
  * 资源为对象，对象的属性：
    * 以查询为例：
      * 目标单个属性：`GET /user/{username}/password`
      * 目标多个属性：`GET /user/{username}/password,birthday`(参见上一大节的URL部分，这是HTTP协议已明确编写的)
* 资源层级关系
  * 认识到资源的层级关系。 如`/users/{username}/todos`
  * 避免无关资源层级关联。
* 可省略的资源限定片段
  * 使用已具备前置状态要求
    * 如：当前用户`/users/{current_username}/settings`可以直接写为`/settings`，`/settings`一定是关联到人的，如果系统业务十分丰富`settings`这个宽泛的名字可能有撞车的风险则需要有必要取一个概念更窄的资源名
* `RESTFUL`常见使用疑难
  * 背景：大多数支持restful的web框架，只能依靠`HTTP Method` + `url` + `header` + `param`区别API，但是OAS似乎只支持使用`HTTP Method`  
    + `url`区别API 
  * 资源 + HTTP动词的形式如何精细化的描述业务场景
    * Q1: `GET /users`，怎么满足既可以返回分页数据，又能返回不分页数据（例如，业务数据明确不多的情况下做页面下拉选项）
    * A1: 一般来讲数据都是要分页的，如果确实业务上不会有很多数据，比如部门，则可以从`/department`改为`/department_options`
    * Q2: `POST /users/{username}/password`假设为新增密码，那么验证密码怎么编写？
    * A2: 动词可确定为`POST`，但这里资源同样也是`/users/{username}/password`就无法区别了，这时应该将资源名从实体名修改为服务名，即
          `POST password_validation { username: lzx, current_pwd: pwd, submit_pwd: pwd}`
# RESTFUL总结
* 这里称`/users/{username}/todos`这种使用层级即可表现的资源转化为简易模式
* 简易模式兼具简要&结构关系&状态变成的表现力，这种模式应该优先使用
* 简易模式无法覆盖所有情况，此时只能使用领域服务名来做资源名称