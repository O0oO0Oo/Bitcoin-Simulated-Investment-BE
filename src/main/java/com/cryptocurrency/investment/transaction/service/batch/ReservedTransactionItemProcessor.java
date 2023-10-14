package com.cryptocurrency.investment.transaction.service.batch;

import com.cryptocurrency.investment.transaction.dto.processing.ReservedTransactionJpaDto;
import com.cryptocurrency.investment.user.domain.UserAccount;
import com.cryptocurrency.investment.user.repository.UserRepository;
import com.cryptocurrency.investment.wallet.domain.Wallet;
import com.cryptocurrency.investment.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.util.*;

@Component
@RequiredArgsConstructor
public class ReservedTransactionItemProcessor implements ItemProcessor<Object, Object> {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;

    private Map<UUID, Wallet> walletMap = new HashMap<>();
    private Map<UUID, UserAccount> userAccountMap = new HashMap<>();
    private List<Long> transactionIds = new ArrayList<>();

    public Map<UUID, Wallet> getWalletMap() {
        return walletMap;
    }
//    public List<UUID>
    public Map<UUID, UserAccount> getUserAccountMap() {
        return userAccountMap;
    }
    public List<Long> getTransactionIds() {
        return transactionIds;
    }

    public void clear(){
        walletMap.clear();
        userAccountMap.clear();
        transactionIds.clear();
    }

    @Override
    public Object process(Object item) throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.wrap(((ReservedTransactionJpaDto) item).getUser_account_id());
        UUID id = new UUID(byteBuffer.getLong(), byteBuffer.getLong());
        if (!(item instanceof ReservedTransactionJpaDto)) {
            return null;
        }

        // 트랜잭션의 상태를 완료로 업데이트하기 위한 배열
        transactionIds.add(((ReservedTransactionJpaDto) item).getId());

        // 예약 구매가 매칭되면 유저의 지갑에 코인을 추가
        if (((ReservedTransactionJpaDto) item).getType().equals("RESERVE_BUY")) {
            if(walletMap.containsKey(id)){
                Wallet wallet = walletMap.get(id);

                wallet.setAmount(wallet.getAmount() + ((ReservedTransactionJpaDto) item).getAmount());
                wallet.setTotalCost(wallet.getTotalCost() +
                        ((ReservedTransactionJpaDto) item).getAmount() * ((ReservedTransactionJpaDto) item).getPrice());
                walletMap.put(id, wallet);
            }
            else{
                Optional<Wallet> walletOpt = walletRepository.findByUserAccount_IdAndName(
                        id,
                        ((ReservedTransactionJpaDto) item).getName()
                );
                if (walletOpt.isPresent()) {
                    Wallet wallet = walletOpt.get();
                    wallet.setAmount(wallet.getAmount() + ((ReservedTransactionJpaDto) item).getAmount());
                    wallet.setTotalCost(wallet.getTotalCost() +
                            ((ReservedTransactionJpaDto) item).getAmount() * ((ReservedTransactionJpaDto) item).getPrice());
                    walletMap.put(id, wallet);
                }
                else{

                }
            }
        // 예약 판매가 매칭되면 유저의 계정에 돈을 추가
        } else if (((ReservedTransactionJpaDto) item).getType().equals("RESERVE_SELL")) {
            if(userAccountMap.containsKey(id)){
                UserAccount userAccount = userAccountMap.get(id);
                userAccount.setMoney(userAccount.getMoney() +
                        ((ReservedTransactionJpaDto) item).getAmount() * ((ReservedTransactionJpaDto) item).getPrice());
                userAccountMap.put(id, userAccount);
            }
            else {
                Optional<UserAccount> userAccountOpt = userRepository.findById(id);

                if (userAccountOpt.isPresent()) {
                    UserAccount userAccount = userAccountOpt.get();
                    userAccount.setMoney(userAccount.getMoney() +
                            ((ReservedTransactionJpaDto) item).getAmount() * ((ReservedTransactionJpaDto) item).getPrice()
                    );
                    userAccountMap.put(id, userAccount);
                }
            }
        }
        return null;
    }
}
