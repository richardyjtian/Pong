#include "FreeRTOS.h"
#include "task.h"
#include "SSD2119.h"
#include "tiDefs.h"
#include <stdio.h>

// Initialize the GPIO Port F for on board buttons
void GPIO_Init(void);

// Initialize PLL to 80 MHz
void PLL_Init();

// Initialize UARTs
void UARTInit();

// Transmit string through UART1
void TransmitData(char* data);

// Recieve string through UART2
void ReadData(void *p);

// Parse through string 
void recieveData(char in);

// Handle user bat
void PortF_Handler (void *p);

// Move ball to x, y
void updateBall(unsigned short x, unsigned short y);

// Move user bat (1 == left, 0 == right)
void updateBar(short dir);

// Shift elements in array over by 1 and push in num
void push(int *array, int num);

// Move phone bat
void updatePhoneBar(unsigned short x);

static unsigned short boardX; // Tiva bat
static unsigned short phoneX; // Phone bar

// Do not modify this function.
//
// This hook is called by FreeRTOS when an stack overflow error is detected.
void vApplicationStackOverflowHook(xTaskHandle *pxTask, char *pcTaskName) {
  // This function can not return, so loop forever.  Interrupts are disabled
  // on entry to this function, so no processor interrupts will interrupt
  // this loop.
  while (1) {}
} 

// Initialize FreeRTOS and start the tasks.
int main(void) {
  PLL_Init();
  UARTInit();
  GPIO_Init();
  LCD_Init();
  LCD_ColorFill(convertColor(0, 255, 0));
  boardX = 140;
  LCD_DrawFilledRect(140, 3, 40, 6, convertColor(255, 255, 255));
  phoneX = 140;
  LCD_DrawFilledRect(140, 231, 40, 6, convertColor(255, 255, 255));

  // xTaskCreate(Function Name,
  //             Descriptive Task Name,
  //             Stack Depth,
  //             Task Function Parameter,
  //             Priority,
  //             Task Handle);
  xTaskCreate(PortF_Handler, (const char *)"Buttons", 1024, NULL, 2, NULL);
  xTaskCreate(ReadData, (const char *)"ReadIncomingData", 1024, NULL, 1, NULL);

  // Start the scheduler. This should not return.
  vTaskStartScheduler();

  // In case the scheduler returns for some reason, loop forever.
  while(1) {}
}

void PLL_Init() {
  RCC2 |= 0x80000000; // override RCC values
  RCC &= ~0x1; // main osc on
  RCC2 |= 0x800; // enable bypass;
  RCC &= ~(0x7C0); // clear xtal
  RCC |= (0x540); // 16 MHz xtal
  RCC2 &= ~(0x70); // clear osculator selector
  RCC |= 0x400000; // usesysdiv 0 for 4, 1 for 80
  RCC2 |= 0x40000000; // div400 on
  RCC2 &= ~(0x2000); // pwrdn2 = normal
  RCC2 &= ~0x1FC00000; // clear sysdiv
  RCC2 |= 0x1000000; // set sysdiv
  
  while (!(RIS & 1<<6)) {} // wait for pll lock
  RCC2 &= ~(0x800); // clear bypass2
}


void UARTInit() {
  
  RCGCGPIO |= 0xC; // enable clock to Port C and D
  
  GPIOCAFSEL |= 0x30; // allow alternative function for PC 5 (Bluetooth TX)
  GPIOCDEN |= 0x30; // enable digital pins
  GPIOCPCTL |= 0x220000; // set alternative function to UART
  
  GPIODAFSEL |= 0x40 + 0x80; // allow alternative function for PD 6 (Bluetooth RX)
  GPIODDEN |= 0x40 + 0x80; // enable digital pins
  GPIODPCTL |= 0x11000000; // set alternative function to UART

  RCGCUART |= 0x2 + 0x4; // enable clock to UART Module 1 and 2
  volatile unsigned long delay = RCGCUART;   // delay to wait for clock setup

  UART1CTL &= ~(0x1); // diable module before configuration
  UART2CTL &= ~(0x1); // diable module before configuration

  // set the baud rate (integer & fraction parts)
  UART1IBRD = 0x2B; // for 80 MHz clock
  UART1FBRD = 0x1A; // for 80 MHs clock
  UART2IBRD = 0x2B; // for 80 MHz clock
  UART2FBRD = 0x1A; // for 80 MHs clock
  
  UART1LCRH = 0x60; // set the transmission line control
  UART1CC = 0x0; // use system clock
  UART2LCRH = 0x60; // set the transmission line control
  UART2CC = 0x0; // use system clock
  
  UART1CTL |= 0x1; // enable UART Module
  UART2CTL |= 0x1; // enable UART Module
}

void GPIO_Init() {
  RCGCGPIO = PF_ON; // enable Port F GPIO
  volatile unsigned long delay = RCGCGPIO; // allow time for clock to start
  GPIOFDIR = 0x0; // Set all F pins to input
  
  GPIOFLOCK = UNLOCK_GPIO; // Unlocks GPIO so you can write onto GPIOFCR
  GPIOFCR = SWITCH1 + SWITCH2; // Allows GPIOFPUR to be written to 
  
  GPIOFDEN |= SWITCH1; // enable digital pin for Switch 1
  GPIOFDEN |= SWITCH2; // enable digital pin for Switch 2
  GPIOFDATA = 0; // set port F pins to 0
  
  
  GPIOFPUR = SWITCH1 + SWITCH2; // Enables pin 0 and 4 pull up resistor
  GPIOFCR = 0x0; // resets  GPIOFCR
  GPIOFLOCK = 0x0; // locks GPIO
}

void TransmitData(char* data) {
  char* temp = data; // data to be transmitted
  
  while(*temp != '\0') {
    while ((UART1FR & 0x8)) {} // waits if uart is busy

    UART1DR = *temp; // transmits one character
    temp++;
  }
}

void ReadData(void *p) {
  char temp;
  
  while (1) {
    while (!(UART2FR & 0x10)) { // waits for a something to come in
      temp = (char) UART2DR; // this variable is here for debugging purposes
      recieveData(temp);
    }
    
    vTaskDelay(1); 
  }
  
}

void recieveData(char in) {
  static int xCoord[3], yCoord[3];
  static int comma; // controls if number gets added to xCoord or yCoord
  
  if (in == ';') {
    updateBall(xCoord[2] * 100 + xCoord[1] * 10 + xCoord[0], yCoord[2] * 100 + yCoord[1] * 10 + yCoord[0]); // draw ball
     comma = 0; 
    // reset coordinates
    xCoord[0] = 0;
    xCoord[1] = 0;
    xCoord[2] = 0;
    yCoord[0] = 0;
    yCoord[1] = 0;
    yCoord[2] = 0;
  } else if (in == '!') {
    updatePhoneBar(xCoord[2] * 100 + xCoord[1] * 10 + xCoord[0]); // move bottom bat
     comma = 0; 
    // reset coordinates
    xCoord[0] = 0;
    xCoord[1] = 0;
    xCoord[2] = 0;
    yCoord[0] = 0;
    yCoord[1] = 0;
    yCoord[2] = 0;
  } else {
    if (in == ',') {
      comma = 1;
    } else if (in == ' ') {
      // ignore space
    } else {
      if (comma) push(yCoord, (int) (in - 48));
      else push(xCoord, (int) (in - 48));
    }
    
  }
  
  
}

// shift elements in array over by 1 and push in num
void push(int *array, int num) { // size 3 array only
    array[2] = array[1];
    array[1] = array[0];
    array[0] = num;
}

// 23.92/6 width for ball

void PortF_Handler (void *p) {
  static unsigned int currTickF, prevTickF;
  static char bar[4];
  bar[3] = '!';
  currTickF = xTaskGetTickCount();
  prevTickF = currTickF;
  
  while (1) {
    currTickF = xTaskGetTickCount();
    
    if (!(GPIOFDATA & SWITCH1) && (GPIOFDATA & SWITCH2)) {
      if (currTickF - prevTickF < 10) { 
        // nothing
      } else { // left
        if (boardX != 0) {
          updateBar(1);
          bar[0] = (char) ((boardX / 100) % 10 + 48);
          bar[1] = (char) ((boardX / 10) % 10 + 48);
          bar[2] = (char) (boardX % 10 + 48);
          TransmitData(bar);
        }
        
        prevTickF = currTickF;
      }
    } else if ((GPIOFDATA & SWITCH1) && !(GPIOFDATA & SWITCH2)) {
      if (currTickF - prevTickF < 10) { 
        // nothing
      } else { // right
        if (boardX != 280) {
          updateBar(0);
          bar[0] = (char) ((boardX / 100) % 10 + 48);
          bar[1] = (char) ((boardX / 10) % 10 + 48);
          bar[2] = (char) (boardX % 10 + 48);
          TransmitData(bar);
        }
        
        prevTickF = currTickF;
      }
    } else prevTickF = currTickF;;
    
    vTaskDelay(1); 
  }
}

void updateBall(unsigned short x, unsigned short y) {
  static unsigned short oldX, oldY;
  LCD_DrawFilledCircle(oldX, oldY, 3, convertColor(0, 255, 0)); // fill old ball
  LCD_DrawFilledCircle(x, y, 3, convertColor(255, 255, 255)); // drawball
  oldX = x;
  oldY = y;
}

void updateBar(short dir) {
  // updates only edge pixels to reduce the number of pixels being refreshed each call
  if (dir) { // dir == 1 == left
    LCD_DrawLine(boardX + 40, 3, boardX + 40, 8, convertColor(0, 255, 0));
    LCD_DrawLine(boardX - 1, 3, boardX - 1, 8, convertColor(255, 255, 255));
    boardX--;
  } else { // dir == 0 == right
    LCD_DrawLine(boardX - 1, 3, boardX - 1, 8, convertColor(0, 255, 0));
    LCD_DrawLine(boardX + 40, 3, boardX + 40, 8, convertColor(255, 255, 255));
    boardX++;
  }
}

void updatePhoneBar(unsigned short x) {
  // uses for loop to update bar so if it missed a coordinate update, it'll make up for it
  if (x < phoneX) { // left
    for (int i = 0; i < (phoneX - x); i++) {
      LCD_DrawLine(phoneX + 40 - i, 231, phoneX + 40 - i, 236, convertColor(0, 255, 0));
      LCD_DrawLine(phoneX - 1 - i, 231, phoneX - 1 - i, 236, convertColor(255, 255, 255));
    }
  } else if (x > phoneX) { // right
    for (int i = 0; i < (x - phoneX); i++) {
      LCD_DrawLine(phoneX - 1 + i, 231, phoneX - 1 + i, 236, convertColor(0, 255, 0));
      LCD_DrawLine(phoneX + 40 + i, 231, phoneX + 40 + i, 236, convertColor(255, 255, 255));
    }
  }
  
  phoneX = x;
}


