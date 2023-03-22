
import UIKit
import PayWithMyBank

class MerchantViewController: UIViewController {

    var payWithMyBankView = PayWithMyBankView()
    var establishData:Dictionary<AnyHashable,Any> = [:]
    var payWithMyBankPanel = PayWithMyBankView()
    var MERCHANT_ID = "1051"
    var ACCESS_ID = "WS8vT7ftpHImaeLB5FSx"
    var APP_DEEP_LINK = "com.palainteractive.stardustcasino.paywithmybank://"
    
    var delegate: PayWithMyBankViewProtocol?

    override func viewDidLoad() {
        super.viewDidLoad()

        /**
         * @todo establishData should be populated by Javascript Cordova call
         */
        self.establishData["metadata.urlScheme"]=APP_DEEP_LINK;
        self.establishData["metadata.integrationContext"]="InAppBrowser";
        
        self.payWithMyBankView.onChangeListener { (eventName, attributes) in
            if let event = eventName, let data = attributes {
                print("PWMB: MerchantViewController: onChangeListener: \(event) \(data)")
            }
        }
        self.view = self.payWithMyBankView
        
        let _ = self.payWithMyBankView.selectBankWidget(establishData) { (view, data) in
            if let data = data {
                print("PWMB: MerchantViewController: returnParameters:\(data)")
                self.establishData = data
                self.pay()
            }
        }

    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    func pay() {

        let trustlyLightboxViewController = TrustlyLightboxViewController()
        trustlyLightboxViewController.delegate = self

        trustlyLightboxViewController.establishData = establishData
//        trustlyLightboxViewController.establishData!["amount"] = nil;
        
        self.present(trustlyLightboxViewController, animated: true)
    }
    
}

extension MerchantViewController: TrustlyLightboxViewProtocol {
    
    func onReturnWithTransactionId(transactionId: String, controller: TrustlyLightboxViewController) {
        controller.dismiss(animated: true)
        self.delegate?.onReturnWithTransactionId(transactionId: transactionId)
    }
    
    func onCancelWithTransactionId(transactionId: String?, controller: TrustlyLightboxViewController) {
        controller.dismiss(animated: true)
        self.delegate?.onCancelWithTransactionId(transactionId: transactionId)
    }

}
