# BYO-Tech

A collection of tools for technical stuff

## GIT

In order to share git hooks, the **core.hooksPath** (git >= 2.9) is used on a versioned directory. Therefore, in
order to use them, you still need to configure the path locally:
``git config --local core.hooksPath .githooks/``.
