#ifndef TIDEFS

#define TIDEFS

#define RCGCGPIO (*((volatile unsigned long *) 0x400FE608)) // Turns on ports by attaching a clock to it, see datasheet for more details
#define GPIOFDIR (*((volatile unsigned long *) 0x40025400)) // Controls if a port F pin is an output or input (0 == input)
#define GPIOFDEN (*((volatile unsigned long *) 0x4002551C)) // Controls if a port F pin is digital or analog (1 == digital)
#define GPIOFDATA (*((volatile unsigned long *) 0x400253FC)) // Has the on/off values of pins i  port F
#define GPIOFPUR (*((volatile unsigned long *) 0x40025510)) // Controls if a port F pin has it's pull up resistor enabled
#define GPIOFCR (*((volatile unsigned long *) 0x40025524)) // Controls if GPIOAFSEL, GPIOPUR, GPIOPDR, and GPIODEN for a port F pin can be written to
#define GPIOFLOCK (*((volatile unsigned long *) 0x40025520)) // Controls if GPIOAFSEL, GPIOPUR, GPIOPDR, and GPIODEN for a port F pin can be written to
#define GPIOFIM (*((volatile unsigned long *) 0x40025410)) // enable flag
#define GPIOFICR (*((volatile unsigned long *) 0x4002541C)) // clear flag
#define GPIOFRIS (*((volatile unsigned long *) 0x40025414)) // check pin
#define GPIOFIS (*((volatile unsigned long *) 0x40025404)) // set edge or level
#define GPIOIEV (*((volatile unsigned long *) 0x4002540C)) // set trigger high or low

#define GPIOAPCTL (*((volatile unsigned long *) 0x4000452C)) 
#define GPIOAAMSEL (*((volatile unsigned long *) 0x40004528))
#define GPIOADIR (*((volatile unsigned long *) 0x40004400)) // Controls if a port A pin is an output or input (0 == input)
#define GPIOAAFSEL (*((volatile unsigned long *) 0x40004420))
#define GPIOADEN (*((volatile unsigned long *) 0x4000451C)) // Controls if a port A pin is digital or analog (1 == digital)
#define GPIOADATA (*((volatile unsigned long *) 0x400043FC))// Has the on/off values of pins i  port A
#define GPIOAIM (*((volatile unsigned long *) 0x40004410)) // enable flag
#define GPIOAICR (*((volatile unsigned long *) 0x4000441C)) // clear flag
#define GPIOARIS (*((volatile unsigned long *) 0x40004414)) // check pin
#define GPIOAIS (*((volatile unsigned long *) 0x40004404)) // set edge or level
#define GPIOAIEV (*((volatile unsigned long *) 0x4000440C)) // set trigger high or low

#define GPIOBPCTL (*(volatile uint32_t *)0x4000552C)
#define GPIOBDIR (*((volatile unsigned long *) 0x40005400)) // Controls if a port B pin is an output or input (0 == input)
#define GPIOBAFSEL (*((volatile unsigned long *) 0x40005420)) 
#define GPIOBDEN (*((volatile unsigned long *) 0x4000551C)) // Controls if a port A pin is digital or analog (1 == digital)

#define GPIODAFSEL (*(volatile uint32_t *)0x40007420)
#define GPIODDEN (*(volatile uint32_t *)0x4000751C)
#define GPIODPCTL (*(volatile uint32_t *)0x4000752C)

#define GPIOCAFSEL (*(volatile uint32_t *)0x40006420)
#define GPIOCDEN (*(volatile uint32_t *)0x4000651C)
#define GPIOCPCTL (*(volatile uint32_t *)0x4000652C)

// Timer0 configerations
#define RCGCTIMER (*((volatile unsigned long *) 0x400FE604))
#define T0GPTMCTL (*((volatile unsigned long *) 0x4003000C))
#define T0GPTMCFG (*((volatile unsigned long *) 0x40030000))
#define T0GPTMTAMR (*((volatile unsigned long *) 0x40030004))
#define T0GPTMTAILR (*((volatile unsigned long *) 0x40030028))
#define T0GPTMIMR (*((volatile unsigned long *) 0x40030018))
#define T0GPTMRIS (*((volatile unsigned long *) 0x4003001C))
#define T0GPTMICR (*((volatile unsigned long *) 0x40030024))
#define T0GPTMTAV (*((volatile unsigned long *) 0x40030050))
#define T0GPTMTAPR (*((volatile unsigned long *) 0x40030038))
#define EN0 (*((volatile unsigned long *) 0xE000E100))
#define RESETTIMER 0x1 
#define ONESEC 0xF42400 // One second in 16 MHZ
#define TWOSEC 0x1E84800 // Two seconds in 16 MHz
#define FIVESEC 0x4C4B400 // Five seconds in 16 MHZ

// ADC
#define RCGCADC (*((volatile unsigned long *) 0x400FE638))
#define RCC (*((volatile unsigned long *) 0x400FE060))
#define RCC2 (*((volatile unsigned long *) 0x400FE070))
#define RCC2DIV (*((volatile unsigned long *) 0x400FE072)) // [31:4]
#define RIS (*((volatile unsigned long *) 0x400FE050))
// ADC0
#define ADC0ISC (*((volatile unsigned long *) 0x4003800C))
#define ADC0ACTSS (*((volatile unsigned long *) 0x40038000))
#define ADC0EMUX (*((volatile unsigned long *) 0x40038014))
#define ADC0SSCTL3 (*((volatile unsigned long *) 0x400380A4))
#define ADC0SSMUX3 (*((volatile unsigned long *) 0x400380A0))
#define ADC0ACTSS (*((volatile unsigned long *) 0x40038000))
#define ADC0PSSI (*((volatile unsigned long *) 0x40038028))
#define ADC0RIS (*((volatile unsigned long *) 0x40038004))
#define ADC0SSFIFO3 (*((volatile unsigned long *) 0x400380A8))
#define AD0CIM (*((volatile unsigned long *) 0x40038008))

// UART
#define RCGCUART (*((volatile unsigned long *) 0x400FE618))
#define UART0CTL (*((volatile unsigned long *) 0x4000C030))
#define UART0IBRD (*((volatile unsigned long *) 0x4000C024))
#define UART0FBRD (*((volatile unsigned long *) 0x4000C028))
#define UART0CC (*((volatile unsigned long *) 0x4000CFC8))
#define UART0LCRH (*((volatile unsigned long *) 0x4000C02C))
#define UART0CT (*((volatile unsigned long *) 0x4000C030))
#define UART0FR (*((volatile unsigned long *) 0x4000C018))
#define UART0DR (*((volatile unsigned long *) 0x4000C000))

#define UART1CTL            (*(volatile uint32_t *)0x4000D030)
#define UART1LCRH           (*(volatile uint32_t *)0x4000D02C)
#define UART1CC             (*(volatile uint32_t *)0x4000DFC8)
#define UART1IBRD           (*(volatile uint32_t *)0x4000D024)
#define UART1FBRD           (*(volatile uint32_t *)0x4000D028)
#define UART1DR             (*(volatile uint32_t *)0x4000D000)
#define UART1FR             (*(volatile uint32_t *)0x4000D018)

#define UART2CTL            (*(volatile uint32_t *)0x4000E030)
#define UART2LCRH           (*(volatile uint32_t *)0x4000E02C)
#define UART2CC             (*(volatile uint32_t *)0x4000EFC8)
#define UART2IBRD           (*(volatile uint32_t *)0x4000E024)
#define UART2FBRD           (*(volatile uint32_t *)0x4000E028)
#define UART2DR             (*(volatile uint32_t *)0x4000E000)
#define UART2FR             (*(volatile uint32_t *)0x4000E018)
#define UART2ERROR             (*(volatile uint32_t *)0x4000E004)


#define UNLOCK_GPIO 0x4C4F434B // Value needed to unlock gpio
#define LOCK_GPIO 0x0 // Any value other than UNLOCK_GPIO locks it
#define RED 0x2 // red led pin onbaord
#define BLUE 0x4 // blue led pin onbaord
#define GREEN 0x8// green led pin onbaord
#define PF_ON 0x20 // port F on gpio
#define PA5_ON 0x20 // pin 5, port A
#define PA_ON 0x01 // port A on gpio
#define PA2_ON 0x04 // pin 2, port A
#define SWITCH1 0x10 // switch 1 on board
#define SWITCH2 0x1 // switch 2 on board
#define EXBUTTONS_ON 0x60 // External buttons on pins 5 and 6
#define EXLEDS_ON 0x1C // External LEDS on pins 2-4
#define EXBUTTON1 0x20 // External button on pin 5
#define EXBUTTON2 0x40 // External button on pin 6
#define EXGREEN 0x10 // external green led on pin 4
#define EXYELLOW 0x8 // external yellow led on pin 3
#define EXRED 0x4 // external red led on pin 2
#define TRUE 1
#define FALSE 0

void LED_and_switch_init();
void onState();

void ledRun();
void ledAndSwitchInit();

void trafficLight();
void ledInit();
void goState();
void warnState();
void stopState();
short switch1Pressed();
short switch2Pressed();
void setUpTimer();
void blinkBlue();
void enableLightShow();
void enableExButtonInterrupt();

void Timer0_Init(unsigned long speed);
void PortF_Init();
void PLL_Init();
void ADC_Init();
void ADC0_Handler();
void LED_Change(float temp);
void UART_Init(float speed);
void UART_Trans(long number);
void UART_TransChar(char character);
void changeTimer(unsigned long speed);
void PLLChange(unsigned long speed);
void changeUART(unsigned long speed);

#endif // TIDEFS