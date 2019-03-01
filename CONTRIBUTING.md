Hi! Welcome to Jdbi.

We're glad you're thinking about contributing to the project.

Here's a few pointers to help you get started:

# Project philosophy

We value:

- Fluent interfaces
- Backward compatibility
- Minimal API surface
- Configurability
- Vendor neutrality
- Visible magic
- Separation of concerns
- Hand-written SQL over generated SQL
- Transparent JDBC resource management

## Intuitive interfaces

Above all, we strive to make Jdbi a pleasant, intuitive library to use.

We favor fluent interfaces that "flow," which usually translates into APIs that use chaining method
calls, e.g.

```java
List<Customer> customers = handle.select("select * from customer where id = :id")
      .bind("id", id)
      .mapTo(Customer.class)
      .list();
``` 

We take pains to get API names and usage patterns just right before merging.

Note: APIs annotated `@Beta` are not finalized, and are subject to change.

## Backward compatibility

Jdbi places serious emphasis on not breaking compatibility. Remember these simple rules and think
twice before making any classes or class members `public`!

1) what comes into the API, stays in the API (or: no is temporary, but yes is forever);
2) if a piece of API must be discouraged after public release, mark it `@Deprecated` and keep it
   functionally intact;
3) breaking cleanup work can be done when Jdbi is gearing up for a major version number increment
   (see [SemVer](https://semver.org/));
4) bug fixes that **absolutely require** an API change are the only exception.

## Minimal API surface

While working on Jdbi 2.x, we found that making too many APIs `public` can shackle the project to
past design mistakes (at least if you mind breaking backward compatibility).

Accordingly, when we started work on 3.x, we resolved to minimize public API as much as is
practical.

This policy has enabled us to refactor parts of Jdbi's internals that would have been frozen had
we made them `public`.

* Use `public` and `protected` access modifiers only for the minimum set of classes and methods
  required to serve and configure a feature.
* Make `public` classes `final` if they are not designed to be extended. If they _are_ designed to
  be extended, they should probably be `abstract`--but exceptions can be made depending on context.
* If you must make some internal code `public` to access it from other Jdbi packages, put the class
  in a package named `internal`. Packages so named are excluded from javadocs, and not considered
  API.

This policy comes with a trade-off: occasionally you may want to override Jdbi's out-of-the-box
behavior, but cannot because the behavior is hard-coded in code that is hidden.

We try to mitigate these limitations by making Jdbi highly configurable.

## Configurability

We try to make Jdbi's out-of-the-box behavior work for most use cases.



When those defaults don't work for your use case, Jdbi 

When those defaults don't fit your needs, Jdbi strives to be configurable 
However, when our defaults don't fit your needs, Jdbi should be configurable you should be able to configure Jdbi to suit
your needs.

When contributing a feature that should be configurable, try to look past your present use case and
consider whether the default you prefer makes sense for most users.

If there's no sensible default for most cases, it's reasonable to have no default, and force users
to set an explicit default before using the feature.

The standard method for adding new configuration options to Jdbi is by implementing the
[JdbiConfig](http://jdbi.org/#_jdbiconfig) interface.

Note: changing out-of-the-box defaults for a non-`@Beta` API is considered a backward-breaking change.

## Vendor neutrality

## Separation of concerns

## Hand-written SQL over generated SQL

## Transparent JDBC resource management

Many objects in JDBC have a lifecycle that needs to be actively managed to prevent database
resource leaks. In practice this means closing `Statement`s, `ResultSet`s, and freeing `Array`s
when you are done with them.

When possible, we handle this complexity for you so you don't have to think about it.

If 

We strive to handle this complexity for you--for those resources we know about, at any rate.

For new resources (e.g. vendor-specific JDBC objects) the StatementContext.addCleanable(Cleanable)
method 

# Design principles

- Make no assumptions about the application environment Jdbi will be used (e.g. Guice, Spring Boot,
  Tomcat)
- Everything is configurable.
- Separate API and implementation details. Keep implementation details hidden, so we can refactor
  without breaking API compatibility.
- Out of the box defaults should be sane and widely shared.
- Avoid direct support for boutique features, but add API hooks 


# Policies

## Compatibility

### Forward

Completely new API should, in most cases, be marked with `@Beta`. This lets users know not to rely
too much on your changes yet, as the public release might reveal that more work needs to be done.

## Functionality

Jdbi should be useful for as many projects as possible with as little work as possible, within
reason. It should be useful out of the box with sane defaults, but always configurable to the
extent users are likely to need.

## Technical design

We like both constructors and factory methods/builders, but require that they be used
appropriately. Constructors are great for dumb classes, factories are better in case any defensive
logic is involved.

Remember to implement thread safety wherever objects are likely to be shared between threads, but
don't implement it where it definitely isn't needed. Making objects stateless or immutable is
strongly encouraged!

## Testing

Unit tests are nice for atomic components, but since jdbi is a complex ecosystem of components, we
prefer to use tests that spin up real jdbi instances and make it work against an in-memory
database. This ensures all code is covered by many different test cases and almost no flaw will go
unnoticed.

Since our tests essentially describe and verify jdbi's behavior, changing their specifics where it
isn't inherently necessary is considered a red flag.

## Pull requests

Expect your pull request to be scrutinized in even the tiniest details (down to grammar in
javadoc), and to need to address many remarks even for small changes. We strive for a healthy
balance between subjective perfection and practical considerations, but we are firmly against doing
a quick and sloppy job that will require a lot of follow-up work later.

If your pull request adds new API, be conservative in what you make public. We intentionally hide
as much implementation as possible to reduce the 

Due to the volume of feedback in a typical PR, we may push changes directly to your PR branch if we
are able to, in order to save time and frustration for everyone.

### Separation of concerns

Keep pull requests small, and focused on a single issue. Conflating multiple concerns in the same
PR will complicate the review process, and 

 

### Jdbi team policy

* Delete branches after merge

# Setup

## Enable `-parameters` compiler flag

Most of our SQL Object tests rely on SQL method parameter names. However by default, `javac` does
not compile these parameter names into `.class` files. Thus, in order for unit tests to pass, the
compiler must be configured to output parameter names.

## Configure import ordering

We enforce this order of imports:

```
java.*
javax.*
*
static java.*
static javax.*
static *
```

A blank line is required between each group. The imports in a group must be ordered
alphabetically. Wildcard imports (e.g. `import org.apache.*;`) are not allowed.

### IntelliJ

* File &rarr; Settings
* Build, Execution, Deployment &rarr; Compiler &rarr; Java Compiler
* Additional command-line parameters: `-parameters`
* Click Apply, then OK.
* Build &rarr; Rebuild Project
