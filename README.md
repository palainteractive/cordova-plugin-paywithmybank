# cordova-plugin-paywithmybank
#### Pala Interactive - Internal documentation
#### author: Dan Shields
#### created: 2023-03-21

# quick overview:

This Cordova plugin is for integration of the Trustly (Pay With My Bank) product with our B2B and Stardust customers.

### status:

Both the Android and iOS implementations are passing local tests.

The web implementation has passed, videos demonstrating iOS and Android ExpiredSplitToken test have been submitted.

### installation in ionic 4:

```bash
npm install --save https://github.com/palainteractive/cordova-plugin-paywithmybank
```

### installation in ionic 2:

```bash
cordova add --save https://github.com/palainteractive/cordova-plugin-paywithmybank
```

note: if there are github authentication issues with cordova add due to older versions of cordova-cli, it may be necessary to use:

```bash
git clone https://github.com/palainteractive/cordova-plugin-paywithmybank
cordova add --save ./cordova-plugin-paywithmybank
```
