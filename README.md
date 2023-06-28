# cordova-plugin-paywithmybank (Trustly)
#### Pala Interactive - Internal documentation
#### author: Dan Shields
#### created: 2023-03-21

# quick overview:

This Cordova plugin is for integration of the Trustly (Pay With My Bank) product with our B2B and Stardust customers.

### status:

Both the Android and iOS implementations are passing local tests.

The web implementation has passed, videos demonstrating iOS and Android ExpiredSplitToken test have been submitted.

### installation in ionic 4:

  - You must set your GH_TOKEN shell var to a token that has access to the repository (we have a role account on GitHub `palainteractivedev` that issues tokens for the build machines).

```bash
npm install --save https://github.com/palainteractive/cordova-plugin-paywithmybank
```

### installation in ionic 2:

```bash
bash builds/odr-cordova-plugin-add.sh cordova-plugin-paywithmybank --save
```
